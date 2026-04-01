public class HospitalMap{
    char [][] grid;
    int rows, cols;
    
    //start position of A and B
    int saRow, saCol;
    int sbRow, sbCol;
    //target position of A and B
    int taRow, taCol;
    int tbRow, tbCol;

    public HospitalMap(String[] lines){
        rows = lines.length;
        cols = lines[0].length();
        grid = new char[rows][cols];

        //检查地图是否为矩形
        for(int r = 0; r < rows; r ++){
            if(lines[r].length() != cols){
                throw new IllegalArgumentException("Map is not rectangular");
            }

            //把每一行的字符拿出来根据类别添加到grid中
            for(int c = 0; c < cols; c ++){
                char ch = lines[r].charAt(c); 

                switch(ch){
                    case '#':
                        grid[r][c] = '#';
                        break;

                    case '.':
                        grid[r][c] = '.';
                        break;

                    case 'A':
                        grid[r][c] = '.';
                        saRow = r;
                        saCol = c;
                        break;

                    case 'B':
                        grid[r][c] = '.';
                        sbRow = r;
                        sbCol = c;
                        break;

                    case 'a':
                        grid[r][c] = '.';
                        taRow = r;
                        taCol = c;
                        break;

                    case 'b':
                        grid[r][c] = '.';
                        tbRow = r;
                        tbCol = c;
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown map symbol: " + ch);
                }
            }
        }
    }
    //检查坐标是否在地图内部
    public boolean inBounds(int row, int col){
        return row >= 0 && col >= 0 &&
               row < rows && col < cols;
    }
    //检查是否为可行走的空地
    public boolean isFree(int row, int col){
        return inBounds(row, col) && grid[row][col] != '#';
    }
}
