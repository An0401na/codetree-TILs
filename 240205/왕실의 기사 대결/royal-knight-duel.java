import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main{
    static int L; // 체스판크기
    static int N; // 기사의 수
    static int Q; // 명령의 수
    static int board[][];
    static class Knight{
        int top;
        int bottom;
        int left;
        int right;
        int hp;
        int damage;
        boolean isDie;

        public Knight(int top, int bottom, int left, int right, int hp, int damage) {
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
            this.hp = hp;
            this.damage = damage;
            this.isDie = false;
        }

        @Override
        public String toString() {
            return "Knight{" +
                    "top=" + top +
                    ", bottom=" + bottom +
                    ", left=" + left +
                    ", right=" + right +
                    ", hp=" + hp +
                    ", damage=" + damage +
                    ", isDie=" + isDie +
                    '}';
        }
    }
    static Knight knights[];
    static boolean pitfallBoard[][];
    static final int WALL = -1;
    static int dir[][] = {{-1,0}, {0,1},{1,0},{0,-1}};//위, 오른쪽, 아래, 왼쪽
    static int total;
    static boolean canMove;
    static int knight;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        board = new int[L+2][L+2];
        pitfallBoard = new boolean[L+2][L+2];

        // 보드 입력
        for (int i = 1; i <= L; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= L; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
                if(board[i][j] == 1){
                    board[i][j] = 0;
                    // 함정은 boolean 이차원 배열을 따로 만들어 따로 표시
                    pitfallBoard[i][j] = true;
                }
                if(board[i][j] == 2){
                    // 벽은 기사와 구분되게 -1로 변경
                    board[i][j] = WALL;
                }
            }
        }

        Arrays.fill(board[0], WALL);
        Arrays.fill(board[L+1], WALL);
        for (int i = 0; i < L+2; i++) {
            board[i][0] = WALL;
            board[i][L+1] =WALL;
        }
        knights = new Knight[N+1];
        //기사 입력
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());

            knights[i] = new Knight(r, r+h, c, c+w, k, 0);
        }

        for (int i = 1; i < N+1; i++) {
            if(!knights[i].isDie){
                Knight k = knights[i];
                for (int r = k.top; r < k.bottom; r++) {
                    for (int c = k.left; c < k.right; c++) {
                        board[r][c] = i;
                    }
                }
            }
        }
//        System.out.println();
//        printBoard();


        // 명령 수행
        for (int q = 0; q < Q; q++) {
            st = new StringTokenizer(br.readLine());

//            System.out.println("\n-------- " +q+"번째 명령 ---------");
            int idx = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            if(knights[idx].isDie) continue;


            canMove = true;
            // 기사를 한칸 d 방향으로 이동 했을 때
            // 벽에 부딪혀서 못가는지 확인
            checkMove(idx ,d);
            if(canMove){
//                System.out.println("이동 가능");
                // 진짜로 이동
                knight = idx;
                move(idx, d);
            }else{
//                System.out.println("이동 못함");
            }

//            printBoard();
//            printKnights();
        }

        // 생존한 기사들의 총 받은 대미지의 합
        getTotalDamege();
        System.out.println(total);
    }

    private static void move(int idx, int d) {

//        System.out.println(idx +"번 기사 이동 "+d+"로 이동");
        Knight k = knights[idx];
        // 이동할 위치 범위 지정
        int top = k.top+ dir[d][0];
        int bottom = k.bottom + dir[d][0];
        int left = k.left + dir[d][1];
        int right = k.right + dir[d][1];


        int damageCnt = 0;
        Loop1 : for (int i = top; i < bottom; i++) {
            for (int j = left; j < right; j++) {

                //pitfallBoard도 같이 확인하여 true 이면 데미지 올리기
                if(knight!= idx && pitfallBoard[i][j]){ // 함정이 있는데
//                    System.out.println(idx +"번 이동하다가 "+ i+", "+j+"번에 함정!");
//                    System.out.println(top+", " + bottom+", "+left+", " + right);
                    // 밀쳐진 위치인 경우
                    if(k.top > i || i >= k.bottom || k.left > j || k.right <= j) {
                        // 추가 데미지 증가
                        damageCnt++;
                    }
                }
                if(board[i][j] > 0 && board[i][j] != idx){
                    // 이동해야할 위치가 0보다 크고 본인의 idx가 아닌경우 -> 다른 기사
                    // 연쇄적으로  그 기사의 move 실행
                    move(board[i][j], d);
                }
            }
        }

//        System.out.println(idx +"번 기사 얻은 데미지 :" + damageCnt);
        k.damage += damageCnt;
        if(k.damage  >= k.hp ){
            k.isDie = true;
            coloring(idx, d, 0);
        }else{
            // 진짜 이동시 보드 색칠
            coloring(idx, d, idx);
        }
    }

    private static void coloring(int idx, int d, int color) {
        // 진짜 이동시 보드 색칠

        Knight k = knights[idx];
        if(d == 0){ // 위로
            for (int i = k.left; i < k.right; i++) {
                board[k.top-1][i] = color;
                board[k.bottom-1][i] = 0;
            }
        }


        if(d == 1){ // 오른쪽
            for (int i = k.top; i < k.bottom; i++) {
                board[i][k.right] = color;
                board[i][k.left] = 0;
            }
        }


        if(d == 2){ // 아래로
            for (int i = k.left; i < k.right; i++) {
                board[k.top][i] = 0;
                board[k.bottom][i] = color;
            }
        }


        if(d == 3){ // 왼쪽으로
            for (int i = k.top; i < k.bottom; i++) {
                board[i][k.right-1] = 0;
                board[i][k.left-1] = color;
            }
        }
//        System.out.println("dddd");
//        printBoard();


        // 진짜 이동시 보드 색칠 후 top, bottom,left, right 재정의

        // 이동할 위치 범위 지정
        k.top = k.top+ dir[d][0];
        k.bottom = k.bottom + dir[d][0];
        k.left = k.left + dir[d][1];
        k.right = k.right + dir[d][1];


    }

    public static void checkMove(int idx, int d) {

//        System.out.println(idx +"번 기사 이동 "+d+"로 이동체크");
        Knight k = knights[idx];
        // 이동할 위치 범위 지정
        int top = k.top+ dir[d][0];
        int bottom = k.bottom + dir[d][0];
        int left = k.left + dir[d][1];
        int right = k.right + dir[d][1];

        // 벽에 부딪혀서 못가는지 기사에 부딪혀서 못가는지 확인

        Loop1 : for (int i = top; i < bottom; i++) {
            for (int j = left; j < right; j++) {
                if (!canMove) break Loop1;
                if(board[i][j] == WALL){  // 벽이 있는경우 연쇄된 모든 기사들 못움직임
//                    System.out.println(idx + "번호 이동하다가"+i+", "+j+"에 막힘");
                    canMove = false;
                    break Loop1;
                }
                if(board[i][j] > 0 && board[i][j] != idx){
                    // 이동해야할 위치가 0보다 크고 본인의 idx가 아닌경우 -> 다른 기사
                    // 연쇄적으로 밀기
                    // 그 기사의 checkMove 실행
                    checkMove(board[i][j], d);
                }
            }
        }


        // 이동해야하는 모든 위치가 0또는 자기 idx라면 연쇄 이동 멈추고 진짜 이동
        // check에 들어가지 않고 포문을 빠져나온 경우
    }



    private static void printBoard() {
        for (int i = 0; i < L+2; i++) {
            System.out.println(Arrays.toString(board[i]));

        }
    }

    private static void printKnights() {

        for (int i = 1; i < N+1; i++) {
            System.out.println(knights[i].toString());
        }
    }

    private static void draw() {
        int map[][] = new int[L+2][L+2];
        for (int i = 0; i < L+2; i++) {
            map[i] =Arrays.copyOf(board[i], L+2);
        }

        for (int i = 1; i < N+1; i++) {
            if(!knights[i].isDie){
                Knight k = knights[i];
                for (int r = k.top; r < k.bottom; r++) {
                    for (int c = k.left; c < k.right; c++) {
                        if(pitfallBoard[r][c]){
                            map[r][c] = i *-1;
                        }else {
                            map[r][c] = i;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < L+2; i++) {
            System.out.println(Arrays.toString(map[i]));
        }
    }

    private static void getTotalDamege() {
        for (int i = 1; i < N+1; i++) {
            if(!knights[i].isDie){
                total += knights[i].damage;
            }
        }
    }
}