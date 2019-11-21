package com.secureai.system;

import com.secureai.rl.abs.TerminateFunction;

public class SystemTerminateFunction implements TerminateFunction<SystemState> {

    @Override
    public boolean terminated(SystemState systemState) {
        for (int i = 0; i < systemState.size(); i++)
            if (!systemState.get(i, NodeState.active) || !systemState.get(i, NodeState.updated) || systemState.get(i, NodeState.vulnerable) || systemState.get(i, NodeState.corrupted))
                return false;

        return true;
    }

}