import java.util.ArrayList;
import java.util.List;

public class PdfStyleSolution {
    private int R, C, P;

    // idOf[r][c] = free cell id at coordinate (r, c), or -1 if it is a wall.
    private int[][] idOf;

    // rowOf[id], colOf[id] = coordinates of a free cell id.
    private int[] rowOf, colOf;

    // Start and target ids of the two robots.
    private int SA, SB, TA, TB;

    /*
     * Idea:
     * Scan the whole map, give every free cell a unique id from 0 to P-1,
     * and build two-way mapping:
     * 1) coordinate -> id
     * 2) id -> coordinate
     * At the same time, record the start and target ids of A and B.
     */
    public void indexFreeCells(String[] map) {
        R = map.length;
        C = map[0].length();

        idOf = new int[R][C];
        for (int r = 0; r < R; r++) {
            if (map[r].length() != C) {
                throw new IllegalArgumentException("Map is not rectangular.");
            }
            for (int c = 0; c < C; c++) {
                idOf[r][c] = -1;
            }
        }

        P = 0;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                if (map[r].charAt(c) != '#') {
                    P++;
                }
            }
        }

        rowOf = new int[P];
        colOf = new int[P];

        int nextId = 0;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                char ch = map[r].charAt(c);
                if (ch == '#') {
                    continue;
                }

                idOf[r][c] = nextId;
                rowOf[nextId] = r;
                colOf[nextId] = c;

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
    public List<Integer> computeSuccessors(int id) {
        List<Integer> successors = new ArrayList<>();

        int r = rowOf[id];
        int c = colOf[id];

        int[] dR = {-1, 0, 1, 0, 0};
        int[] dC = {0, 1, 0, -1, 0};

        for (int i = 0; i < 5; i++) {
            int nr = r + dR[i];
            int nc = c + dC[i];

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
    public boolean isLegalTransition(int a, int b, int a2, int b2) {
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
    public int solve(String[] map, int T) {
        indexFreeCells(map);

        List<List<Integer>> succ = new ArrayList<>();
        for (int id = 0; id < P; id++) {
            succ.add(computeSuccessors(id));
        }

        boolean[][][] reachable = new boolean[T + 1][P][P];
        reachable[0][SA][SB] = true;

        if (SA == TA && SB == TB) {
            return 0;
        }

        for (int t = 1; t <= T; t++) {
            for (int a = 0; a < P; a++) {
                for (int b = 0; b < P; b++) {
                    if (!reachable[t - 1][a][b]) {
                        continue;
                    }

                    for (int a2 : succ.get(a)) {
                        for (int b2 : succ.get(b)) {
                            if (isLegalTransition(a, b, a2, b2)) {
                                reachable[t][a2][b2] = true;
                            }
                        }
                    }
                }
            }

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

        PdfStyleSolution solver = new PdfStyleSolution();
        System.out.println(solver.solve(map, 10));
    }
}
