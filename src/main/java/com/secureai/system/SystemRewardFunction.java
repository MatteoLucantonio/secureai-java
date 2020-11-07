package com.secureai.system;

import com.secureai.Config;
import com.secureai.DynDQNMain;
import com.secureai.model.actionset.Action;
import com.secureai.rl.abs.RewardFunction;
import lombok.Getter;

public class SystemRewardFunction implements RewardFunction<SystemState, SystemAction> {

    private SystemEnvironment environment;

    @Getter
    private double maxExecutionTime;
    @Getter
    private double maxExecutionCost;

    public SystemRewardFunction(SystemEnvironment environment) {
        this.environment = environment;

        this.maxExecutionTime = this.environment.getActionSet().getActions().values().stream().map(Action::getExecutionTime).max(Double::compareTo).orElse(0d);
        this.maxExecutionCost = this.environment.getActionSet().getActions().values().stream().map(Action::getExecutionCost).max(Double::compareTo).orElse(0d);
    }

    @Override
    public double reward(SystemState oldState, SystemAction systemAction, SystemState currentState) {

        Action action = this.environment.getActionSet().getActions().get(systemAction.getActionId());
        //if (oldState.equals(currentState))

        if(oldState.equals(currentState) && systemAction.checkPreconditions(this.environment, action) != true) {
            if(!DynDQNMain.training)
                System.out.println("Not executable action has been selected: "+systemAction.getActionId());
            return -2; // This is the reward if the policy choose an action that cannot be run or keeps the system in the same state
        }

        /*
        if(environment.isDone())
        {
            System.out.println("Terminal State!");
            return 0;
        }*/

        return -(Config.TIME_WEIGHT * (action.getExecutionTime() / this.maxExecutionTime) + Config.COST_WEIGHT * (action.getExecutionCost() / this.maxExecutionCost));
    }

}
