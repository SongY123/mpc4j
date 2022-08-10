package edu.alibaba.mpc4j.s2pc.pcg.ot.base.np01;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.alibaba.mpc4j.common.rpc.MpcAbortException;
import edu.alibaba.mpc4j.common.rpc.MpcAbortPreconditions;
import edu.alibaba.mpc4j.common.rpc.Party;
import edu.alibaba.mpc4j.common.rpc.Rpc;
import edu.alibaba.mpc4j.common.rpc.utils.DataPacket;
import edu.alibaba.mpc4j.common.rpc.utils.DataPacketHeader;
import edu.alibaba.mpc4j.common.tool.crypto.ecc.Ecc;
import edu.alibaba.mpc4j.common.tool.crypto.ecc.EccFactory;
import edu.alibaba.mpc4j.common.tool.crypto.kdf.Kdf;
import edu.alibaba.mpc4j.common.tool.crypto.kdf.KdfFactory;
import edu.alibaba.mpc4j.s2pc.pcg.ot.base.AbstractBaseOtReceiver;
import edu.alibaba.mpc4j.s2pc.pcg.ot.base.BaseOtReceiverOutput;
import edu.alibaba.mpc4j.s2pc.pcg.ot.base.np01.Np01BaseOtPtoDesc.PtoStep;
import org.bouncycastle.math.ec.ECPoint;

/**
 * NP01-基础OT协议接收方。
 *
 * @author Weiran Liu, Hanwen Feng
 * @date 2019/06/17
 */
public class Np01BaseOtReceiver extends AbstractBaseOtReceiver {
    /**
     * 配置项
     */
    private final Np01BaseOtConfig config;
    /**
     * 椭圆曲线
     */
    private final Ecc ecc;
    /**
     * C
     */
    private ECPoint upperC;
    /**
     * g^r
     */
    private ECPoint g2r;
    /**
     * 选择比特对应的消息
     */
    private byte[][] rbArray;

    public Np01BaseOtReceiver(Rpc receiverRpc, Party senderParty, Np01BaseOtConfig config) {
        super(Np01BaseOtPtoDesc.getInstance(), receiverRpc, senderParty, config);
        this.config = config;
        ecc = EccFactory.createInstance(envType);
    }

    @Override
    public void init() throws MpcAbortException {
        setInitInput();
        info("{}{} Recv. Init begin", ptoBeginLogPrefix, getPtoDesc().getPtoName());

        initialized = true;
        info("{}{} Recv. Init end", ptoEndLogPrefix, getPtoDesc().getPtoName());
    }

    @Override
    public BaseOtReceiverOutput receive(boolean[] choices) throws MpcAbortException {
        setPtoInput(choices);
        info("{}{} Recv. begin", ptoBeginLogPrefix, getPtoDesc().getPtoName());

        stopWatch.start();
        DataPacketHeader initHeader = new DataPacketHeader(
                taskId, ptoDesc.getPtoId(), PtoStep.SENDER_SEND_INIT.ordinal(), extraInfo,
                otherParty().getPartyId(), ownParty().getPartyId()
        );
        List<byte[]> initPayload = rpc.receive(initHeader).getPayload();
        handleSenderPayload(initPayload);
        stopWatch.stop();
        long initTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
        stopWatch.reset();
        info("{}{} Recv. Step 1/2 ({}ms)", ptoStepLogPrefix, getPtoDesc().getPtoName(), initTime);

        stopWatch.start();
        List<byte[]> publicKeyPayload = generateReceiverPayload();
        DataPacketHeader publicKeyHeader = new DataPacketHeader(
                taskId, ptoDesc.getPtoId(), PtoStep.RECEIVER_SEND_PK.ordinal(), extraInfo,
                ownParty().getPartyId(), otherParty().getPartyId()
        );
        rpc.send(DataPacket.fromByteArrayList(publicKeyHeader, publicKeyPayload));
        stopWatch.stop();
        long pkTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
        stopWatch.reset();
        info("{}{} Recv. Step 2/2 ({}ms)", ptoStepLogPrefix, getPtoDesc().getPtoName(), pkTime);

        info("{}{} Recv. end", ptoEndLogPrefix, getPtoDesc().getPtoName());
        BaseOtReceiverOutput receiverOutput = new BaseOtReceiverOutput(choices, rbArray);
        rbArray = null;

        return receiverOutput;
    }

    private void handleSenderPayload(List<byte[]> senderPayload) throws MpcAbortException {
        MpcAbortPreconditions.checkArgument(senderPayload.size() == 2);
        // 解包g^r、C
        g2r = ecc.decode(senderPayload.remove(0));
        upperC = ecc.decode(senderPayload.remove(0));
    }

    private List<byte[]> generateReceiverPayload() {
        Kdf kdf = KdfFactory.createInstance(envType);
        rbArray = new byte[choices.length][];
        // 公钥生成流
        IntStream publicKeyIntStream = IntStream.range(0, choices.length);
        publicKeyIntStream = parallel ? publicKeyIntStream.parallel() : publicKeyIntStream;
        List<byte[]> receiverPayload = publicKeyIntStream
                .mapToObj(index -> {
                    // The receiver picks a random k
                    BigInteger k = ecc.randomZn(secureRandom);
                    // The receiver sets public keys PK_{\sigma} = g^k
                    ECPoint pkSigma = ecc.multiply(ecc.getG(), k);
                    // and PK_{1 - \sigma} = C / PK_{\sigma}
                    ECPoint pkOneMinusSigma = upperC.add(pkSigma.negate());
                    // 存储OT的密钥key=H(index,g^rk)
                    byte[] kInputByteArray = ecc.encode(ecc.multiply(g2r, k), false);
                    rbArray[index] = kdf.deriveKey(ByteBuffer
                            .allocate(Integer.BYTES + kInputByteArray.length)
                            .putInt(index).put(kInputByteArray)
                            .array());
                    // 返回密钥
                    return choices[index] ? pkOneMinusSigma : pkSigma;
                })
                .map(pk -> ecc.encode(pk, config.getCompressEncode()))
                .collect(Collectors.toList());
        upperC = null;
        g2r = null;

        return receiverPayload;
    }
}
