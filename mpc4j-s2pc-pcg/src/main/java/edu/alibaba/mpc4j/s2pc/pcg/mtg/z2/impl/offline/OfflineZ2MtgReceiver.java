package edu.alibaba.mpc4j.s2pc.pcg.mtg.z2.impl.offline;

import edu.alibaba.mpc4j.common.rpc.MpcAbortException;
import edu.alibaba.mpc4j.common.rpc.Party;
import edu.alibaba.mpc4j.common.rpc.PtoState;
import edu.alibaba.mpc4j.common.rpc.Rpc;
import edu.alibaba.mpc4j.s2pc.pcg.mtg.z2.AbstractZ2MtgParty;
import edu.alibaba.mpc4j.s2pc.pcg.mtg.z2.Z2Triple;
import edu.alibaba.mpc4j.s2pc.pcg.mtg.z2.core.Z2CoreMtgConfig;
import edu.alibaba.mpc4j.s2pc.pcg.mtg.z2.core.Z2CoreMtgFactory;
import edu.alibaba.mpc4j.s2pc.pcg.mtg.z2.core.Z2CoreMtgParty;

import java.util.concurrent.TimeUnit;

/**
 * offline Z2 multiplication triple generator receiver.
 *
 * @author Weiran Liu
 * @date 2022/4/8
 */
public class OfflineZ2MtgReceiver extends AbstractZ2MtgParty {
    /**
     * core multiplication triple generator
     */
    private final Z2CoreMtgParty coreMtgReceiver;
    /**
     * max base num
     */
    private final int maxBaseNum;
    /**
     * num per round per update
     */
    private int updateRoundNum;
    /**
     * round per update
     */
    private int updateRound;
    /**
     * triple buffer
     */
    private Z2Triple tripleBuffer;

    public OfflineZ2MtgReceiver(Rpc receiverRpc, Party senderParty, OfflineZ2MtgConfig config) {
        super(OfflineZ2MtgPtoDesc.getInstance(), receiverRpc, senderParty, config);
        Z2CoreMtgConfig coreMtgConfig = config.getCoreMtgConfig();
        coreMtgReceiver = Z2CoreMtgFactory.createReceiver(receiverRpc, senderParty, coreMtgConfig);
        addSubPtos(coreMtgReceiver);
        maxBaseNum = coreMtgConfig.maxNum();
    }

    public OfflineZ2MtgReceiver(Rpc receiverRpc, Party senderParty, Party aiderParty, OfflineZ2MtgConfig config) {
        super(OfflineZ2MtgPtoDesc.getInstance(), receiverRpc, senderParty, config);
        Z2CoreMtgConfig coreMtgConfig = config.getCoreMtgConfig();
        coreMtgReceiver = Z2CoreMtgFactory.createReceiver(receiverRpc, senderParty, aiderParty, coreMtgConfig);
        addSubPtos(coreMtgReceiver);
        maxBaseNum = coreMtgConfig.maxNum();
    }

    @Override
    public void init(int updateNum) throws MpcAbortException {
        setInitInput(updateNum);
        logPhaseInfo(PtoState.INIT_BEGIN);

        stopWatch.start();
        if (updateNum <= maxBaseNum) {
            // we only need to run one round
            updateRoundNum = updateNum;
            updateRound = 1;
        } else {
            // we need to run multiple rounds
            updateRoundNum = maxBaseNum;
            updateRound = (int) Math.ceil((double) updateNum / maxBaseNum);
        }
        coreMtgReceiver.init(updateRoundNum);
        tripleBuffer = Z2Triple.createEmpty();
        stopWatch.stop();
        long initTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
        stopWatch.reset();
        logStepInfo(PtoState.INIT_STEP, 1, 2, initTime);

        // generate triple in offline phase
        for (int round = 1; round <= updateRound; round++) {
            stopWatch.start();
            Z2Triple triple = coreMtgReceiver.generate(this.updateRoundNum);
            tripleBuffer.merge(triple);
            stopWatch.stop();
            long roundTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
            stopWatch.reset();
            logSubStepInfo(PtoState.INIT_STEP, 2, round, updateRound, roundTime);
        }

        logPhaseInfo(PtoState.INIT_END);
    }

    @Override
    public Z2Triple generate(int num) throws MpcAbortException {
        setPtoInput(num);
        logPhaseInfo(PtoState.PTO_BEGIN);

        while (num > tripleBuffer.getNum()) {
            // generate if we do not have enough triples
            for (int round = 1; round <= updateRound; round++) {
                stopWatch.start();
                Z2Triple triple = coreMtgReceiver.generate(updateRoundNum);
                tripleBuffer.merge(triple);
                stopWatch.stop();
                long roundTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
                stopWatch.reset();
                logSubStepInfo(PtoState.PTO_STEP, 0, round, updateRound, roundTime);
            }
        }

        stopWatch.start();
        Z2Triple receiverOutput = tripleBuffer.split(num);
        stopWatch.stop();
        long splitTripleTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
        stopWatch.reset();
        logStepInfo(PtoState.PTO_STEP, 1, 1, splitTripleTime);

        logPhaseInfo(PtoState.PTO_END);
        return receiverOutput;
    }
}
