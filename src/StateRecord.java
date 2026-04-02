public class StateRecord {
    State state;
    StateRecord previous;

    //保存状态路径用于回溯得到完整路径
    public StateRecord(State state, StateRecord previous){
        this.state = state;
        this.previous = previous;
    }
}
