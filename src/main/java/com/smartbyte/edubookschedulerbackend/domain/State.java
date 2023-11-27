package com.smartbyte.edubookschedulerbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@Getter
public enum State {
    Requested(0){
        @Override
        public HashMap<State, List<Role>> getNextModifiableState(){
            HashMap<State,List<Role>>hashMap=new HashMap<>();

            hashMap.put(Scheduled,List.of(Role.Tutor));
            hashMap.put(Tutor_Reschedule_Requested,List.of(Role.Tutor));
            hashMap.put(Student_Reschedule_Requested,List.of(Role.Student));
            hashMap.put(Cancelled,List.of(Role.Tutor,Role.Student));

            return hashMap;
        }
    },
    Scheduled(1){
        @Override
        public HashMap<State, List<Role>> getNextModifiableState(){
            HashMap<State,List<Role>>hashMap=new HashMap<>();

            hashMap.put(Tutor_Reschedule_Requested,List.of(Role.Tutor));
            hashMap.put(Student_Reschedule_Requested,List.of(Role.Student));
            hashMap.put(Cancelled,List.of(Role.Tutor,Role.Student));
            hashMap.put(Finished,List.of(Role.Tutor));

            return hashMap;
        }
    },
    Cancelled(2),
    Missed(3){
        @Override
        public HashMap<State, List<Role>> getNextModifiableState(){
            HashMap<State,List<Role>>hashMap=new HashMap<>();

            hashMap.put(Tutor_Reschedule_Requested,List.of(Role.Tutor));
            hashMap.put(Student_Reschedule_Requested,List.of(Role.Student));

            return hashMap;
        }
    },
    Finished(4),
    Tutor_Reschedule_Requested(5){
        @Override
        public HashMap<State, List<Role>> getNextModifiableState(){
            HashMap<State,List<Role>>hashMap=new HashMap<>();

            hashMap.put(Cancelled,List.of(Role.Tutor,Role.Student));
            hashMap.put(Rescheduled,List.of(Role.Student));

            return hashMap;
        }
    },
    Student_Reschedule_Requested(6){
        @Override
        public HashMap<State, List<Role>> getNextModifiableState(){
            HashMap<State,List<Role>>hashMap=new HashMap<>();

            hashMap.put(Cancelled,List.of(Role.Tutor,Role.Student));
            hashMap.put(Rescheduled,List.of(Role.Tutor));

            return hashMap;
        }
    },
    Rescheduled(7);

    private final int stateId;
    public HashMap<State,List<Role>> getNextModifiableState(){
        return new HashMap<>();
    }

    public static State fromStateId(int stateId){
        for (State state :values()){
            if (state.getStateId()==stateId){
                return state;
            }
        }
        throw new IllegalArgumentException("No state with id "+stateId);
    }

}
