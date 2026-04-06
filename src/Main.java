import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] lines = {
            "#########",
            "#A....#B#",
            "#.##.#..#",
            "#...#...#",
            "#.#...#.#",
            "#..#.##.#",
            "#a....#b#",
            "#########"
        };

        HospitalMap map = new HospitalMap(lines);

        State start = new State(map.saRow, map.saCol, map.sbRow, map.sbCol);

        List<State> nextStates = getNextStates(map, start);

        System.out.println("Number of next states: " + nextStates.size());

        for (State s : nextStates) {
            System.out.println("A: (" + s.ax + ", " + s.ay + "), B: (" + s.bx + ", " + s.by + ")");
        }

        System.out.println(hasSolution(map, start));
        System.out.println(shortestMakespan(map, start));

        StateRecord goalRecord = findGoalRecord(map, start);

        //System.out.println(goalRecord != null);
        //System.out.println(goalRecord.previous != null);

        List<State> path = buildPath(goalRecord);
        List<String> schedule = buildSchedule(path);

        for(String step : schedule){
            System.out.println(step);
        }
    }
    
    //还原路径
    public static List<State> buildPath(StateRecord goalRecord){
        List<State> path = new ArrayList<>();

        StateRecord current = goalRecord;
        
        while(current != null){
            path.add(0, current.state);
            current = current.previous;
        }
        return path;
    }

    //还原具体走向
    public static String getMove(int oldRow, int oldCol, int newRow, int newCol){
        if(newRow == oldRow - 1 && newCol == oldCol) return "N";
        if(newRow == oldRow + 1 && newCol == oldCol) return "S";
        if(newRow == oldRow && newCol == oldCol + 1) return "E";
        if(newRow == oldRow && newCol == oldCol - 1) return "W";
        return "WAIT";
    }

    //还原行动日志
    public static List<String> buildSchedule(List<State> path){
        List<String> schedule = new ArrayList<>();

        for(int i = 0; i < path.size() - 1; i ++){
            State current = path.get(i);
            State next = path.get(i + 1);

            String moveA = getMove(current.ax, current.ay, next.ax, next.ay);
            String moveB = getMove(current.bx, current.by, next.bx, next.by);

            schedule.add("(" + moveA + ", " + moveB + ")");
        }
        return schedule;
    }

    //辅助判断位置是否可行
    public static boolean isLegal(HospitalMap map, State current, State next) {
        //next新位置是否可以走
        if(!map.isFree(next.ax, next.ay) || !map.isFree(next.bx, next.by)){
            return false;
        }
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

    //存在多个ticks的时候防止新状态绕回旧状态
    public static boolean sameState(State s1, State s2){
        return s1.ax == s2.ax &&
               s1.ay == s2.ay &&
               s1.bx == s2.bx &&
               s1.by == s2.by;
    } 
    public static boolean containsState(List<State> states, State target){
        for(State s : states){
            if(sameState(s, target)){
                return true;
            }
        }
        return false;
    }

    //这张地图是否存在解使得A,B都到达目标,每n层level就是n个tick
    public static boolean hasSolution(HospitalMap map, State start){
        List<State> currentLevel = new ArrayList<>();
        List<State> nextLevel = new ArrayList<>();
        List<State> visited = new ArrayList<>();

        currentLevel.add(start);
        visited.add(start);

        while(!currentLevel.isEmpty()){
            nextLevel.clear();

            for(State current : currentLevel){
                if(isGoal(map, current)){
                    return true;
                }

                List<State> nextStates = getNextStates(map, current);

                for(State newState : nextStates){
                    if(!containsState(visited, newState)){
                        visited.add(newState);
                        nextLevel.add(newState);
                    }
                }
            }
            //当前层全部检查完以后进入下一层
            currentLevel = new ArrayList<>(nextLevel);
        }

        return false;
    }

    /*
      跟上面的hasSolution结构类似,思路为:按照从小到大的步数暴力列举出所有可能的
      走法,然后能够到达则输出true,因此输出的时候一定是步数最小的情况
    */
    public static int shortestMakespan(HospitalMap map, State start){
        List<State> currentLevel = new ArrayList<>();
        List<State> nextLevel = new ArrayList<>();
        List<State> visited = new ArrayList<>();

        int steps = 0;

        currentLevel.add(start);
        visited.add(start);

        while(!currentLevel.isEmpty()){
            nextLevel.clear();

            for(State current : currentLevel){
                if(isGoal(map, current)){
                    return steps;
                }

                List<State> nextStates = getNextStates(map, current);

                for(State newState : nextStates){
                    if(!containsState(visited, newState)){
                        visited.add(newState);
                        nextLevel.add(newState);
                    }
                }
            }
            //当前层全部检查完以后进入下一层
            currentLevel = new ArrayList<>(nextLevel);
            steps ++;
        }

        return -1;
    }

    //按照状态找记录,相当于把State变成StateRecord
    public static StateRecord findRecord(List<StateRecord> records, State target){
        for(StateRecord record : records){
            if(sameState(record.state, target)){
                return record;
            }
        }
        return null;
    }

    public static StateRecord findGoalRecord(HospitalMap map, State start){
        List<State> currentLevel = new ArrayList<>();
        List<State> nextLevel = new ArrayList<>();
        List<StateRecord> records = new ArrayList<>();
        
        StateRecord startRecord = new StateRecord(start, null);

        currentLevel.add(start);
        records.add(startRecord);

        while(!currentLevel.isEmpty()){
            nextLevel.clear();

            for(State current : currentLevel){
                if(isGoal(map, current)){
                    return findRecord(records, current);
                }

                List<State> nextStates = getNextStates(map, current);
                
                //发现一个不存在记录的新状态,则添加当前的状态为这个新状态的记录
                for(State newState : nextStates){
                    if(findRecord(records, newState) == null){
                        //把终点记录变为链表结构,从终点指向previous直至起点的null
                        StateRecord currentRecord = findRecord(records, current);

                        records.add(new StateRecord(newState,currentRecord));
                        nextLevel.add(newState);
                    }
                }
            }

            currentLevel = new ArrayList<>(nextLevel);
        }

        return null;
    }

    public static List<State> getNextStates(HospitalMap map, State currentState){
        List<State> nextStates = new ArrayList<>();

        //上，右，下，左，不动（x = -1即行减少）
        int[] dX = {-1, 0, 1, 0, 0};
        int[] dY = {0, 1, 0, -1, 0};

        //储存所有可行的位置
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                int newax = currentState.ax + dX[i];
                int neway = currentState.ay + dY[i];
                int newbx = currentState.bx + dX[j];
                int newby = currentState.by + dY[j];

                State newState = new State(newax, neway, newbx, newby);

                if(isLegal(map, currentState, newState)){
                    nextStates.add(newState);
                }
            }
        }
        return nextStates;
    }

    //是否到达终点
    public static boolean isGoal(HospitalMap map, State currentState){
        return currentState.ax == map.taRow &&
               currentState.ay == map.taCol &&
               currentState.bx == map.tbRow &&
               currentState.by == map.tbCol;
    }
}
