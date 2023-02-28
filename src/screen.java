import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;


public class screen extends JFrame implements ActionListener {
    private JTextField inputSpace; //inputSpace 설정
    ArrayList<String> list = new ArrayList<>(); //여기에 동적으로 array에 저장을 해주는 거지!
    public String showtable = ""; //table에 보여질 값 그 때 그 때 저장하는 string
    int count = 0; //연산자가 입력이 될 때
    int countnum = 0;
    int pointcount = 0; //'.'의 개수를 세는 거지
    String result = "";

    Stack<String> historyStack = new Stack<>(); //history 넣어줄 때 필요

    screen() {
        //계산기의 화면과 버튼을 붙임 - 기본 레이아웃 사용
        setLayout(null);

        setTitle("계산기");
        setSize(300, 370);
        setLocationRelativeTo(null); //화면의 가운데 띄움
        setResizable(false); //사이즈조절 불가능
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //창을 닫을 때 실행 중인 프로그램도 같이 종료되도록 함

        inputSpace = new JTextField();//JTextField 만들기
        inputSpace.setEditable(false); //글씨는 버튼을 통해서만 다룰 수 있음
        inputSpace.setBackground(Color.WHITE);
        inputSpace.setFont(new Font("Arial", Font.BOLD, 20));
        inputSpace.setBounds(8, 10, 280, 70);


        JPanel buttonPanel = new JPanel();//버튼을 만들 패널
        buttonPanel.setLayout(new GridLayout(5, 4, 10, 10));//가로 칸수, 세로 칸수, 좌우 간격, 상하 간격
        buttonPanel.setBounds(8, 90, 280, 235); //위치와 크기 설정


        //계산기 버튼의 글자를 차례대로 배열에 저장
        String button_names[] = {"C", "÷", "×", "=", "7", "8", "9", ".", "4", "5", "6", "+", "1", "2", "3", "-", "0", " ", "^", "H"};
        JButton buttons[] = new JButton[button_names.length]; //버튼들의 배열


        //배열을 이용하여 버튼 생성
        for (int i = 0; i < button_names.length; i++) {
            //buttons[i].setOpaque(true);
            buttons[i] = new JButton(button_names[i]);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 20));  //글씨체
            if (buttons[i].getText() == "C") {
                buttons[i].setBackground(Color.red); //왜 버튼 배경색이 안바뀌지
                System.out.println("나와!!!!!");
            }
            buttons[i].setForeground(Color.BLACK); //글자 색 지정
            buttons[i].addActionListener(this);
            buttonPanel.add(buttons[i]); //버튼들을 버튼패널에 추가
        }
        add(inputSpace);
        add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String operation = e.getActionCommand();
        if (operation.equals("C")) {
            count = 0;
            pointcount = 0;
            inputSpace.setText(""); //JTextfield " "으로 만들기
            showtable = "";
        }else if (operation.equals("=")) {
            inputSpace.setText(calculation(operation));
            historyBack(showtable); //showtable 지워지기 전에 보여지기 하나씩! -> history 기능
            showtable = "";
        }else if (operation.equals("+") || operation.equals("-") || operation.equals("×") || operation.equals("÷")) {
            showtable = showtable + operation;
            inputSpace.setText(showtable); //보여지는 table
            //여기다가 넣으면 되겠뉑

            countnum = repeatChar(showtable);
            System.out.println("count : " + count);
            if(countnum > 1){
                showtable = removeLastChar(showtable);
                inputSpace.setText(showtable);
            }
            pointcount = 0; //연산자를 넣어주면 다시 새로운 수가 시작될 수 있으니까 다시 원점으로 돌아가야 함
        }else if(operation.equals(".") ){
            if(showtable.isEmpty()){
                showtable = showtable + "0" + operation;
                inputSpace.setText(showtable); //보여지는 table
            }else{
                showtable = showtable + operation;
                inputSpace.setText(showtable); //보여지는 table
            }

            pointcount += 1;
            System.out.println("point count : " + pointcount);
            if(pointcount > 1){
                showtable = removeLastChar(showtable);
                inputSpace.setText(showtable);
            }
        }else if(operation.equals("H")){
            showtable = historyPrint();
            inputSpace.setText(showtable);
        }else if(operation.equals("^")){
            double square = Double.parseDouble(result);
            double square_value = square*square;

            String s=Double.toString(square_value);

            inputSpace.setText(s);
        }
        else {
            count = 0;
            showtable = showtable + operation;
            inputSpace.setText(showtable); //보여지는 table

        }
    }

    //반복해서 작성되지 못하게 만들기 !!!
    //1. 맨 뒤 바로 앞에 숫자가 있으면 count를 늘리지 않는데,
    //2. 맨 뒤 바로 앞에 operand가 있으면 count를 늘려서 지워지게 하는 거 같은데
    public int repeatChar(String str){
        char operation[] = {'+', '-', '÷', '×'};

        for(int i=0; i<operation.length; i++){
            if(str.charAt(str.length()-1) == operation[i]){
                count = count + 1;
            }
        }
        return count;
    }

    public String removeLastChar(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, str.length() - 1);
    }

    private String calculation(String operation) {
        //사칙연산을 진행 -> showtable에 +,- 을 저장해 준 것을 이제

        if (operation == "=") {
            result = toPostfix(showtable);
            System.out.println("\n결과 :" + result);
            return result;
        }
        return operation;
    }
    //postfix로 변환하는 이유 : 연산자의 우선순위를 파악하기 쉬움 -> x,/ 자체를 먼저 계산하기가 용이해짐


    static int opOrder(char op){
        switch (op){
            case '+' :
            case '-' :
                return 1;
            case '×' :
            case '÷' :
                return 2;
            default:
                return -1;
        }
    }

    private String toPostfix(final String input){
        char operation[] = {'+', '-', '÷', '×'};

        ArrayList<String> postfix = new ArrayList<>(); //
        Stack<Character> opStack = new Stack<>();
        Stack<String> calStack = new Stack<>();
        String num = ""; //피연산자 저장

        for(int i=0; i<input.length(); i++){
            boolean checkOp = false;
            for(int j=0; j<operation.length; j++){
                if(input.charAt(0) == operation[j]){
                    //그 결과를 add postfix로 add 해주면 되는 거 아닌강 ?
                    postfix.add(result);


                }
                if(input.charAt(i) == operation[j]){
                    checkOp = true; //operation과 같은 것이 있다면

                    //내 생각엔 여기를 건들여야 할 꺼 같움 !
                    if(!num.equals("")){ //num이 비어있지 않는다면,
                        postfix.add(num); //피연산자 자체를 postfix에 넣어줌
                        num = "";
                    }

                    if(opStack.isEmpty()){
                        opStack.push(operation[j]); //연산자만 넣어주는 거 !
                    }else{
                        if(opOrder(opStack.peek()) < opOrder(operation[j])){
                            opStack.push(operation[j]);
                        }else{
                            postfix.add(opStack.pop().toString());
                            opStack.push(operation[j]);
                        }
                    }
                }
            }
            if(!checkOp){
                num += input.charAt(i);
            }
        }
        if(!num.equals("")){ //남은 숫자 처리
            postfix.add(num);
        }

        while(!opStack.isEmpty()){ //남은 연산자 처리
            postfix.add(opStack.pop().toString());
        }




        //후위 연산자를 이용해 최종 결과 구하기
        for(int i=0; i<postfix.size(); i++){
            calStack.push(postfix.get(i));
            for(int j=0; j<operation.length; j++){
                if(postfix.get(i).charAt(0) == operation[j]){
                    calStack.pop();
                    Double n2 = Double.parseDouble(calStack.pop());
                    String re = "";

                    Double n1 = Double.parseDouble(calStack.pop());
                    if(operation[j] == '+'){
                        re = Double.toString(n1 + n2);
                    }else if(operation[j] == '-'){
                        re = Double.toString(n1 - n2);
                    }else if(operation[j] == '×'){
                        re = Double.toString(n1 * n2);
                    }else if(operation[j] == '÷'){
                        if(n2 == 0){ //n2이 0이 들어가면 오류를 나오도록
                            return "error";
                        }
                        re = Double.toString(n1 / n2);
                    }
                    calStack.push(re);
                }
            }
        }

        Double result = Double.parseDouble(calStack.pop());
        String calResult[] = Double.toString(result).split("\\."); //.을 기준으로

        if(Double.parseDouble(calResult[1]) == 0){
                return calResult[0];
        }else{
            return String.format("%.5f",result);
        }
    }

    //추가 기능 !! -> 추가기능은 history로 확인을 해보는 거지 !
    private void historyBack(final String input){

        historyStack.push(input); // '='을 누를 때 마다 push를 하는 거지

        for(String str: historyStack)
            System.out.print("historyStack : " + str + " ");

        for(int i = 0; i < historyStack.size(); i++)
            System.out.print(historyStack.get(i) + " ");


    }

    private String historyPrint(){
        if(historyStack.isEmpty()){
            return " ";
        }else{
            String print = historyStack.peek(); //맨 위에 있는 것을 보여주고
            historyStack.pop(); //그다음을 위해 pop을 하구 !
            return print;
        }
    }


}






