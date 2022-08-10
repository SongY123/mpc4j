package edu.alibaba.mpc4j.s2pc.pcg.ot.lo;

import edu.alibaba.mpc4j.common.rpc.MpcAbortException;
import edu.alibaba.mpc4j.common.rpc.pto.SecurePto;
import edu.alibaba.mpc4j.common.rpc.pto.TwoPartyPto;

/**
 * NOT协议发送方接口。
 *
 * @author Weiran Liu
 * @date 2022/5/23
 */
public interface LotSender extends TwoPartyPto, SecurePto {

    @Override
    LotFactory.LotType getPtoType();

    /**
     * 初始化协议。
     *
     * @param inputBitLength 输入比特长度。
     * @param maxNum         最大执行数量。
     * @throws MpcAbortException 如果协议异常中止。
     */
    void init(int inputBitLength, int maxNum) throws MpcAbortException;

    /**
     * 执行协议。
     *
     * @param num 执行数量。
     * @return 发送方输出。
     * @throws MpcAbortException 如果协议异常中止。
     */
    LotSenderOutput send(int num) throws MpcAbortException;
}