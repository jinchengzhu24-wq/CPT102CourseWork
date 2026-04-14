import java.util.ArrayList;
import java.util.List;

public class PdfStyleSolution {
    private static int R, C, P;

    // idOf[r][c] = free cell id at coordinate (r, c), or -1 if it is a wall.
    private static int[][] idOf;

    // rowOf[id], colOf[id] = coordinates of a free cell id.
    private static int[] rowOf, colOf;

    // Start and target ids of the two robots.
    private static int SA, SB, TA, TB;

    /*
     * Idea:
     * Scan the whole map, give every free cell a unique id from 0 to P-1,
     * and build two-way mapping:
     * 1) coordinate -> id
     * 2) id -> coordinate
     * At the same time, record the start and target ids of A and B.
     */
    public static void indexFreeCells(String[] map) {
        //记录地图的行列数
        R = map.length;
        C = map[0].length();

        //映射idOf:从坐标到编号
        idOf = new int[R][C];
        for (int r = 0; r < R; r++) {
            //地图是否为矩形
            if (map[r].length() != C) {
                throw new IllegalArgumentException("Map is not rectangular.");
            }
            //是墙则保留-1,否则分配编号
            for (int c = 0; c < C; c++) {
                idOf[r][c] = -1;
            }
        }

        //统计有多少free cells
        P = 0;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                if (map[r].charAt(c) != '#') {
                    P++;
                }
            }
        }

        //有多少个free cells
        rowOf = new int[P];
        colOf = new int[P];

        //扫描地图
        int nextId = 0;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                char ch = map[r].charAt(c);
                if (ch == '#') {
                    continue;
                }

                //分配编号
                idOf[r][c] = nextId;
                rowOf[nextId] = r;
                colOf[nextId] = c;

                //记录终点的free cells id
                if (ch == 'A') SA = nextId;
                if (ch == 'B') SB = nextId;
                if (ch == 'a') TA = nextId;
                if (ch == 'b') TB = nextId;

                nextId++;
            }
        }
    }

    /*
     * Idea:
     * For one free cell id, convert it back to coordinates, then try
     * N/E/S/W/WAIT and keep only the legal free-cell destinations.
     */
    public static List<Integer> computeSuccessors(int id) {
        //用于存当前free cell后面能到达的所有free cell的id
        List<Integer> successors = new ArrayList<>();

        int r = rowOf[id];
        int c = colOf[id];

        //五种动作
        int[] dR = {-1, 0, 1, 0, 0};
        int[] dC = {0, 1, 0, -1, 0};

        //枚举25种状态
        for (int i = 0; i < 5; i++) {
            int nr = r + dR[i];
            int nc = c + dC[i];

            //没越界且没撞墙
            if (nr >= 0 && nr < R &&
                nc >= 0 && nc < C &&
                idOf[nr][nc] != -1) {
                successors.add(idOf[nr][nc]);
            }
        }

        return successors;
    }

    /*
     * Idea:
     * After single-robot moves are generated, check whether the pair of moves
     * is legal for the two robots together:
     * 1) they cannot end in the same cell
     * 2) they cannot swap positions in one tick
     */
    public static boolean isLegalTransition(int a, int b, int a2, int b2) {
        //两个机器人之间不能同格或者交换位置
        if (a2 == b2) {
            return false;
        }

        if (a == b2 && b == a2) {
            return false;
        }

        return true;
    }

    /*
     * Idea:
     * Use the PDF's array-based method:
     * reachable[t][a][b] is true if after exactly t ticks,
     * A can be at free cell a and B can be at free cell b.
     * Build this layer by layer from t = 0 to T, and return the first t
     * where the target pair (TA, TB) becomes reachable.
     */
    public static int solve(String[] map, int T) {
        /*
          在最多T个tick内,逐层计算哪些联合状态可达
          并返回最早到达目标联合状态的时间。
        */
        //先把地图转化为编号形式
        indexFreeCells(map);

        //统计一个free cell里一步可能到达的free cell id
        List<List<Integer>> succ = new ArrayList<>();
        for (int id = 0; id < P; id++) {
            succ.add(computeSuccessors(id));
        }

        //创建一个三维数组用于判断T个tick后(a,b)是否能到达(TA,TB)
        //从0到T一共T+1层
        boolean[][][] reachable = new boolean[T + 1][P][P];
        //初始化即第0层,初始在起点一定正确
        reachable[0][SA][SB] = true;

        //边界:达到终点
        if (SA == TA && SB == TB) {
            return 0;
        }

        //从第一层开始
        for (int t = 1; t <= T; t++) {
            for (int a = 0; a < P; a++) {
                for (int b = 0; b < P; b++) {
                    //(a,b)是上一层可能存在的联合状态
                    //对于上一层,哪些联合状态是真的可以到达,不可到达则跳过
                    if (!reachable[t - 1][a][b]) {
                        continue;
                    }

                    //(a2,b2)是这一层可能存在的联合状态
                    //若上一层的状态可以到达且下一步移动合理,那么这一层状态就是可能的
                    for (int a2 : succ.get(a)) {
                        for (int b2 : succ.get(b)) {
                            if (isLegalTransition(a, b, a2, b2)) {
                                reachable[t][a2][b2] = true;
                            }
                        }
                    }
                }
            }

            //到达终点即返还T
            if (reachable[t][TA][TB]) {
                return t;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        String[] map = {
            "#######",
            "###a###",
            "###.###",
            "#A...B#",
            "###.###",
            "###b###",
            "#######"
        };

        System.out.println(solve(map, 10));
    }
}
