import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

    }

    public static boolean isLegal(State current, State next) {
        //位置重叠
        if(next.ax == next.bx && next.ay == next.by){
            return false;
        }
        //位置交换
        if(current.ax == next.bx && current.ay == next.by &&
           current.bx == next.ax && current.by == next.ay){
            return false;
        }
        return true;
    }

    public static List<State> getNextStates(State currentState){
        List<State> nextStates = new ArrayList<>();

        //上，右，下，左，不动（x = -1即行减少）
        int[] dX = {-1, 0, 1, 0, 0};
        int[] dY = {0, 1, 0, -1, 0};

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                int newax = currentState.ax + dX[i];
                int neway = currentState.ay + dY[i];;
                int newbx = currentState.bx + dX[j];
                int newby = currentState.by + dY[j];

                State newState = new State(newax, neway, newbx, newby);
                if(isLegal(currentState, newState)){
                    nextStates.add(newState);
                }
            }
        }
        return nextStates;
    }
}
