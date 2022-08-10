package edu.alibaba.mpc4j.s2pc.pcg.ot.lo.hot;

import com.google.common.base.Preconditions;
import edu.alibaba.mpc4j.common.rpc.Rpc;
import edu.alibaba.mpc4j.common.rpc.RpcManager;
import edu.alibaba.mpc4j.common.rpc.impl.memory.MemoryRpcManager;
import edu.alibaba.mpc4j.common.tool.coder.linear.LinearCoder;
import edu.alibaba.mpc4j.common.tool.utils.BytesUtils;
import edu.alibaba.mpc4j.common.tool.utils.CommonUtils;
import edu.alibaba.mpc4j.s2pc.pcg.ot.lo.LotTest;
import edu.alibaba.mpc4j.s2pc.pcg.ot.lo.hot.kk13.Kk13OptLhotConfig;
import edu.alibaba.mpc4j.s2pc.pcg.ot.lo.hot.kk13.Kk13OriLhotConfig;
import edu.alibaba.mpc4j.s2pc.pcg.ot.lo.hot.oos17.Oos17LhotConfig;
import edu.alibaba.mpc4j.s2pc.pcg.ot.lo.LotFactory;
import edu.alibaba.mpc4j.s2pc.pcg.ot.lo.LotReceiverOutput;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 2^l选1-HOT协议测试。
 *
 * @author Weiran Liu
 * @date 2022/6/8
 */
@RunWith(Parameterized.class)
public class LhotTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LotTest.class);
    /**
     * 随机状态
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    /**
     * 默认数量
     */
    private static final int DEFAULT_NUM = 1 << 10;
    /**
     * 较大数量
     */
    private static final int LARGE_NUM = 1 << 16;
    /**
     * 较小输入比特长度
     */
    private static final int SMALL_INPUT_BIT_LENGTH = 1;
    /**
     * 默认输入比特长度
     */
    private static final int DEFAULT_INPUT_BIT_LENGTH = 8;
    /**
     * 较大输入比特长度
     */
    private static final int LARGE_INPUT_BIT_LENGTH = 64;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> configurations() {
        Collection<Object[]> configurations = new ArrayList<>();
        // OOS17
        configurations.add(new Object[] {
            LotFactory.LotType.OOS17.name(), new Oos17LhotConfig.Builder().build(),
        });
        // KK13_OPT
        configurations.add(new Object[] {
            LotFactory.LotType.KK13_OPT.name(), new Kk13OptLhotConfig.Builder().build(),
        });
        // KK13_ORI
        configurations.add(new Object[] {
            LotFactory.LotType.KK13_ORI.name(), new Kk13OriLhotConfig.Builder().build(),
        });

        return configurations;
    }

    /**
     * 发送方
     */
    private final Rpc senderRpc;
    /**
     * 接收方
     */
    private final Rpc receiverRpc;
    /**
     * 协议类型
     */
    private final LhotConfig config;

    public LhotTest(String name, LhotConfig config) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name));
        RpcManager rpcManager = new MemoryRpcManager(2);
        senderRpc = rpcManager.getRpc(0);
        receiverRpc = rpcManager.getRpc(1);
        this.config = config;
    }

    @Test
    public void testPtoType() {
        LhotSender sender = LhotFactory.createSender(senderRpc, receiverRpc.ownParty(), config);
        LhotReceiver receiver = LhotFactory.createReceiver(receiverRpc, senderRpc.ownParty(), config);
        Assert.assertEquals(config.getPtoType(), sender.getPtoType());
        Assert.assertEquals(config.getPtoType(), receiver.getPtoType());
    }

    @Test
    public void test1Num() {
        LhotSender sender = LhotFactory.createSender(senderRpc, receiverRpc.ownParty(), config);
        LhotReceiver receiver = LhotFactory.createReceiver(receiverRpc, senderRpc.ownParty(), config);
        testPto(sender, receiver, DEFAULT_INPUT_BIT_LENGTH, 1);
    }

    @Test
    public void test2Num() {
        LhotSender sender = LhotFactory.createSender(senderRpc, receiverRpc.ownParty(), config);
        LhotReceiver receiver = LhotFactory.createReceiver(receiverRpc, senderRpc.ownParty(), config);
        testPto(sender, receiver, DEFAULT_INPUT_BIT_LENGTH, 2);
    }

    @Test
    public void testDefault() {
        LhotSender sender = LhotFactory.createSender(senderRpc, receiverRpc.ownParty(), config);
        LhotReceiver receiver = LhotFactory.createReceiver(receiverRpc, senderRpc.ownParty(), config);
        testPto(sender, receiver, DEFAULT_INPUT_BIT_LENGTH, DEFAULT_NUM);
    }

    @Test
    public void testParallelDefault() {
        LhotSender sender = LhotFactory.createSender(senderRpc, receiverRpc.ownParty(), config);
        LhotReceiver receiver = LhotFactory.createReceiver(receiverRpc, senderRpc.ownParty(), config);
        sender.setParallel(true);
        receiver.setParallel(true);
        testPto(sender, receiver, DEFAULT_INPUT_BIT_LENGTH, DEFAULT_NUM);
    }

    @Test
    public void testSmallInputBitLength() {
        LhotSender sender = LhotFactory.createSender(senderRpc, receiverRpc.ownParty(), config);
        LhotReceiver receiver = LhotFactory.createReceiver(receiverRpc, senderRpc.ownParty(), config);
        testPto(sender, receiver, SMALL_INPUT_BIT_LENGTH, DEFAULT_NUM);
    }

    @Test
    public void testLargeInputBitLength() {
        LhotSender sender = LhotFactory.createSender(senderRpc, receiverRpc.ownParty(), config);
        LhotReceiver receiver = LhotFactory.createReceiver(receiverRpc, senderRpc.ownParty(), config);
        testPto(sender, receiver, LARGE_INPUT_BIT_LENGTH, DEFAULT_NUM);
    }

    @Test
    public void testLargeNum() {
        LhotSender sender = LhotFactory.createSender(senderRpc, receiverRpc.ownParty(), config);
        LhotReceiver receiver = LhotFactory.createReceiver(receiverRpc, senderRpc.ownParty(), config);
        testPto(sender, receiver, DEFAULT_INPUT_BIT_LENGTH, LARGE_NUM);
    }

    @Test
    public void testParallelLargeNum() {
        LhotSender sender = LhotFactory.createSender(senderRpc, receiverRpc.ownParty(), config);
        LhotReceiver receiver = LhotFactory.createReceiver(receiverRpc, senderRpc.ownParty(), config);
        sender.setParallel(true);
        receiver.setParallel(true);
        testPto(sender, receiver, DEFAULT_INPUT_BIT_LENGTH, LARGE_NUM);
    }

    private void testPto(LhotSender sender, LhotReceiver receiver, int inputBitLength, int num) {
        long randomTaskId = Math.abs(SECURE_RANDOM.nextLong());
        sender.setTaskId(randomTaskId);
        receiver.setTaskId(randomTaskId);
        try {
            LOGGER.info("-----test {} start-----", sender.getPtoDesc().getPtoName());
            int inputByteLength = CommonUtils.getByteLength(inputBitLength);
            byte[][] choices = IntStream.range(0, num)
                .mapToObj(index -> {
                    byte[] choice = new byte[inputByteLength];
                    SECURE_RANDOM.nextBytes(choice);
                    BytesUtils.reduceByteArray(choice, inputBitLength);
                    return choice;
                })
                .toArray(byte[][]::new);
            LhotSenderThread senderThread = new LhotSenderThread(sender, inputBitLength, num);
            LhotReceiverThread receiverThread = new LhotReceiverThread(receiver, inputBitLength, choices);
            StopWatch stopWatch = new StopWatch();
            // 开始执行协议
            stopWatch.start();
            senderThread.start();
            receiverThread.start();
            senderThread.join();
            receiverThread.join();
            stopWatch.stop();
            long time = stopWatch.getTime(TimeUnit.MILLISECONDS);
            stopWatch.reset();
            long senderByteLength = senderRpc.getSendByteLength();
            long receiverByteLength = receiverRpc.getSendByteLength();
            senderRpc.reset();
            receiverRpc.reset();
            LhotSenderOutput senderOutput = senderThread.getSenderOutput();
            LotReceiverOutput receiverOutput = receiverThread.getReceiverOutput();
            // 验证结果
            assertOutput(inputBitLength, num, senderOutput, receiverOutput);
            LOGGER.info("Sender sends {}B, Receiver sends {}B, time = {}ms",
                senderByteLength, receiverByteLength, time
            );
            LOGGER.info("-----test {} end-----", sender.getPtoDesc().getPtoName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void assertOutput(int inputBitLength, int num,
                              LhotSenderOutput senderOutput, LotReceiverOutput receiverOutput) {
        // 验证输入长度
        Assert.assertEquals(inputBitLength, senderOutput.getInputBitLength());
        Assert.assertEquals(inputBitLength, receiverOutput.getInputBitLength());
        Assert.assertEquals(senderOutput.getInputByteLength(), receiverOutput.getInputByteLength());
        // 验证输出长度
        Assert.assertEquals(senderOutput.getOutputBitLength(), receiverOutput.getOutputBitLength());
        Assert.assertEquals(senderOutput.getOutputByteLength(), receiverOutput.getOutputByteLength());
        // 验证数量
        Assert.assertEquals(num, senderOutput.getNum());
        Assert.assertEquals(num, receiverOutput.getNum());
        IntStream.range(0, num).forEach(index -> {
            byte[] choice = receiverOutput.getChoice(index);
            Assert.assertArrayEquals(receiverOutput.getRb(index), senderOutput.getRb(index, choice));
        });
        // 验证同态性，此数量太大，用开根号降低数据量
        LinearCoder linearCoder = senderOutput.getLinearCoder();
        byte[][] choices = receiverOutput.getChoices();
        for (int i = 0; i < num; i += (int)Math.sqrt(num)) {
            for (int j = i + 1; j < num; j += (int)Math.sqrt(num)) {
                byte[] tij = BytesUtils.xor(receiverOutput.getRb(i), receiverOutput.getRb(j));
                byte[] qij = BytesUtils.xor(senderOutput.getQ(i), senderOutput.getQ(j));
                byte[] choicei = BytesUtils.paddingByteArray(choices[i], linearCoder.getDatawordByteLength());
                byte[] choicej = BytesUtils.paddingByteArray(choices[j], linearCoder.getDatawordByteLength());
                byte[] choiceij = BytesUtils.xor(choicei, choicej);
                BytesUtils.xori(qij, BytesUtils.and(senderOutput.getDelta(), linearCoder.encode(choiceij)));
                Assert.assertEquals(ByteBuffer.wrap(tij), ByteBuffer.wrap(qij));
            }
        }
    }
}
