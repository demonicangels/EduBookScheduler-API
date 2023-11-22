package com.smartbyte.edubookschedulerbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum State {
    Requested(0){
        @Override
        public List<State> getNextModifiableState(){
            return List.of(Scheduled,Reschedule_Requested,Cancelled);
        }
    },
    Scheduled(1){
        @Override
        public List<State> getNextModifiableState(){
            return List.of(Reschedule_Requested,Cancelled,Missed,Finished);
        }
    },
    Reschedule_Requested(2){
        @Override
        public List<State> getNextModifiableState(){
            return List.of(Rescheduled,Cancelled);
        }
    },
    Rescheduled(3),
    Cancelled(4),
    Missed(5){
        @Override
        public List<State> getNextModifiableState(){
            return List.of(Reschedule_Requested);
        }
    },
    Finished(6);

    private final int stateId;
    public List<State> getNextModifiableState(){
        return List.of();
    }

    public static State fromStateId(int stateId){
        for (State state :values()){
            if (state.getStateId()==stateId){
                return state;
            }
        }
        throw new IllegalArgumentException("No state with id "+stateId);
    }



    public static boolean isStateValid(int stateId){
        for (State state :values()){
            if (state.getStateId()==stateId){
                return true;
            }
        }
        return false;
    }

}
