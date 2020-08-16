package project1;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Library extends JFrame implements ActionListener {
   ButtonGroup bgroup = new ButtonGroup();
   JRadioButton rbno, rbname, rwname, rbcom, rbyear, runo;

   JButton btnBs, btnCl, btnUs, btnUn, btnRt;
   String[][] bdatas = new String[0][7];
   String[] btitles = { "도서번호", "도서명", "저자명", "출판사", "출판년도", "대여상태", "이용자번호" };
   DefaultTableModel model = new DefaultTableModel(bdatas, btitles);
   JTable table = new JTable(model);
   JTextField st;

   Connection conn;
   PreparedStatement pstmt;
   Statement stmt;
   ResultSet rs, rs1;

   public Library() {
      super("도서정보관리시스템");

      layInit();
      accDb();

      setResizable(true);
      setBounds(400, 400, 1000, 500);
      setVisible(true);

      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            int re = JOptionPane.showConfirmDialog(Library.this, "정말 종료할까요?", "종료", JOptionPane.OK_CANCEL_OPTION);
            if (re == JOptionPane.OK_OPTION) {
               try {
                  if (rs != null)
                     rs.close();
                  if (conn != null)
                     conn.close();
                  if (pstmt != null)
                     pstmt.close();
               } catch (Exception e2) {
                  System.out.println("windowClosing err :" + e);
               }
               System.exit(0);
            } else {
               setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            }
         }
      });

   }

   private void layInit() {
      // 1 north 검색
      rbno = new JRadioButton("도서번호", true);
      rbname = new JRadioButton("도서명", false);
      rwname = new JRadioButton("저자명", false);
      rbcom = new JRadioButton("출판사", false);
      rbyear = new JRadioButton("출판년도", false);
      runo = new JRadioButton("이용자번호", false);
      bgroup.add(rbno);
      bgroup.add(rbname);
      bgroup.add(rwname);
      bgroup.add(rbcom);
      bgroup.add(rbyear);
      bgroup.add(runo);
      JLabel lbl1 = new JLabel();
      st = new JTextField("", 20);

      btnBs = new JButton("검색"); // 도서검색버튼
      btnBs.setBackground(Color.WHITE);
      btnBs.addActionListener(this);
      btnCl = new JButton("초기화"); // 도서검색 초기화
      btnCl.setBackground(Color.WHITE);
      btnCl.addActionListener(this);

      JPanel pn1 = new JPanel();
      pn1.add(rbno);
      pn1.add(rbname);
      pn1.add(rwname);
      pn1.add(rbcom);
      pn1.add(rbyear);
      pn1.add(runo);
      pn1.add(lbl1);
      pn1.add(st);
      pn1.add(btnBs);
      pn1.add(btnCl);
      add("North", pn1);

      // 2 center 리스트
      table.getColumnModel();
      JScrollPane scl = new JScrollPane(table);
      add("Center", scl);

      // 3 south
      JLabel lbl2 = new JLabel("이용자 ", JLabel.LEFT);
      btnUs = new JButton("정보 조회");
      btnUs.setBackground(Color.WHITE);
      btnUs.addActionListener(this);
      btnUn = new JButton("신규 등록");
      btnUn.setBackground(Color.WHITE);
      btnUn.addActionListener(this);
      JLabel lbl3 = new JLabel("도서 대여/반납 ", JLabel.LEFT);
      btnRt = new JButton("대여/반납");
      btnRt.setBackground(Color.WHITE);
      btnRt.addActionListener(this);

      JPanel pn2 = new JPanel();
      pn2.add(lbl2);
      pn2.add(btnUs);
      pn2.add(btnUn);
      pn2.add(lbl3);
      pn2.add(btnRt);
      add("South", pn2);

   }

   private void accDb() {
      try {
         Class.forName("org.mariadb.jdbc.Driver");
         // 자료가 실행하자마자 보여지기
         dispData();
      } catch (Exception e) {
         System.out.println("accDb err : " + e);
      }
   }

   private void dispData() {
      model.setNumRows(0);
      try {
         conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");
         String sql = "SELECT bno, bname, wname, bcom, byear, nvl2(uno, '대여중', '대여가능') AS 대여여부, bookinfo_uno\r\n"
               + "FROM bookinfo\r\n" + "LEFT OUTER JOIN userinfo on bookinfo_uno = uno";
         pstmt = conn.prepareStatement(sql);
         rs = pstmt.executeQuery(sql);

         while (rs.next()) {
            String[] binfo = { rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                  rs.getString(6), rs.getString(7) };
            model.addRow(binfo);
         }
      } catch (Exception e) {
         System.out.println("dispData err :" + e);
      } finally {
         try {
            if (rs != null)
               rs.close();
            if (conn != null)
               conn.close();
            if (pstmt != null)
               pstmt.close();

         } catch (Exception e2) {
            System.out.println("dispData1 err :" + e2);
         }
      }
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      try {
         conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");
         stmt = conn.createStatement();
         String sql = "SELECT bno, bname, wname, bcom, byear, nvl2(uno, '대여중', '대여가능') AS 대여여부, bookinfo_uno\r\n"
               + "FROM bookinfo\r\n" + "LEFT OUTER JOIN userinfo on bookinfo_uno = uno";

         if (e.getSource() == btnBs) {

            // 입력 유효성 검사, 입력된 검색어가 없을때.
            if (st.getText().equals("")) {
               JOptionPane.showMessageDialog(this, "정보를 입력하세요");
               st.requestFocus();
               return;

            } else { // 입력된 검색어가 있을때.
               if (rbno.isSelected()) {// 도서번호으로 검색 조건
                  sql += " where bno like '%" + st.getText().trim() + "%'";

                  rs1 = stmt.executeQuery(sql);

                  if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
                     model.setNumRows(0);
                     while (rs1.next()) {
                        String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
                              rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
                        model.addRow(binfo);
                     }

                  } else {// 없을 때 실행
                     JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
                     st.setText("");
                     return;
                  }

               } else if (rbname.isSelected()) {// 도서명 검색
                  sql += " where bname like '%" + st.getText() + "%'";

                  rs1 = stmt.executeQuery(sql);

                  if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
                     model.setNumRows(0);
                     while (rs1.next()) {
                        String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
                              rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
                        model.addRow(binfo);
                     }

                  } else {// 없을 때 실행
                     JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
                     st.setText("");
                     return;
                  }

               } else if (rwname.isSelected()) { // 저자명으로 검색 조건
                  sql += " where wname like '%" + st.getText() + "%'";

                  rs1 = stmt.executeQuery(sql);

                  if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
                     model.setNumRows(0);
                     while (rs1.next()) {
                        String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
                              rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
                        model.addRow(binfo);
                     }

                  } else {// 없을 때 실행
                     JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
                     st.setText("");
                     return;
                  }

               } else if (rbcom.isSelected()) {// 출판사로 검색 조건
                  sql += " where bcom like '%" + st.getText() + "%'";

                  rs1 = stmt.executeQuery(sql);

                  if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
                     model.setNumRows(0);
                     while (rs1.next()) {
                        String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
                              rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
                        model.addRow(binfo);
                     }

                  } else {// 없을 때 실행
                     JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
                     st.setText("");
                     return;
                  }
               } else if (rbyear.isSelected()) {// 년도로 검색 조건
                  sql += " where byear like '%" + st.getText() + "%'";

                  rs1 = stmt.executeQuery(sql);

                  if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
                     model.setNumRows(0);
                     while (rs1.next()) {
                        String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
                              rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
                        model.addRow(binfo);
                     }

                  } else {// 없을 때 실행
                     JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
                     st.setText("");
                     return;
                  }
               } else if (runo.isSelected()) {// 출판사로 검색 조건
                  sql += " where bookinfo_uno like '%" + st.getText() + "%'";

                  rs1 = stmt.executeQuery(sql);

                  if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
                     model.setNumRows(0);
                     while (rs1.next()) {
                        String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
                              rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
                        model.addRow(binfo);
                     }

                  } else {// 없을 때 실행
                     JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
                     st.setText("");
                     return;
                  }
               }

            }
         } else if (e.getSource() == btnCl) {
            model.setNumRows(0);
            dispData();
            st.setText("");
            st.requestFocus(); // 도서넘버로 초기화
            return;
         } else if (e.getSource() == btnUs) {
            ViewUser ViewUser = new ViewUser(this);

         } else if (e.getSource() == btnUn) { // 이용자신규
            Cuser Cuser = new Cuser(this);
         } else if (e.getSource() == btnRt) { // 이용자신규
            BookRt BookRt = new BookRt(this);
         }

      } catch (Exception e2) {
         System.out.println("자료검색 오류: " + e2);
      }
   }

   // Jdialog 를 위한 내부 무명 클래스
   class ViewUser extends JDialog implements ActionListener {
      // 버튼생성
      JButton btnEn = new JButton("조회");
      JButton btnEc = new JButton("초기화");
      JButton btnEx = new JButton("종료");
      // 텍스트 필드 생성
      JTextField txtUno = new JTextField("", 5);
      JTextField txtUname = new JTextField("", 5);
      JLabel txtUadd;
      JLabel lblb = new JLabel("대여건수 : 0");
      // 대여목록을 위한 배열
      String[][] vdatas = new String[0][3];
      String[] vtitles = { "도서번호", "도서명", "출판년도" };

      DefaultTableModel vmodel = new DefaultTableModel(vdatas, vtitles);
      JTable vtable = new JTable(vmodel);

      Connection conn;
      PreparedStatement pstmt;
      ResultSet rsv, rsv1;

      // 생성자
      public ViewUser(Frame frame) {
         super(frame, "이용자 정보 조회");

         setModal(true);
         setResizable(true);

         // 패널에 텍스트필드 및 라벨 추가
         txtUadd = new JLabel("");

         setLayout(new GridLayout(6, 1));
         JPanel pn1 = new JPanel();
         pn1.add(new JLabel("번호 :", JLabel.LEFT));
         pn1.add(txtUno);
         pn1.add(new JLabel("이름 :", JLabel.LEFT));
         pn1.add(txtUname);
         add("North", pn1);

         JPanel pn2 = new JPanel();
         pn2.add(new JLabel("주소 :", JLabel.LEFT));
         pn2.add(txtUadd);
         add(pn2);

         JPanel pn3 = new JPanel();
         pn3.add(new JLabel("대여목록", JLabel.LEFT));
         add(pn3);

         vtable.getColumnModel().getColumn(0).setPreferredWidth(30);
         JScrollPane scrollPane = new JScrollPane(vtable);
         add(scrollPane);

         JPanel pn4 = new JPanel();
         pn4.add(lblb);
         add(pn4);

         JPanel pn5 = new JPanel();
         btnEn.setBackground(Color.WHITE);
         pn5.add(btnEn);
         btnEc.setBackground(Color.WHITE);
         pn5.add(btnEc);
         btnEx.setBackground(Color.WHITE);
         pn5.add(btnEx);
         btnEn.addActionListener(this);
         btnEc.addActionListener(this);
         btnEx.addActionListener(this);
         add("South", pn5);

         txtUno.setEditable(true);
         txtUname.setEditable(false);

         setBounds(450, 450, 450, 400);
         setVisible(true);

         addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               dispose();
            }
         });
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         if (e.getSource() == btnEn) {
            // 입력자료 오류 검사
            if (txtUno.getText().equals("")) {
               JOptionPane.showMessageDialog(this, "이용자 번호나 이용자 이름을 입력하세요!");
               txtUno.requestFocus();
               return;
            }

            // 이용자 번호 숫자
            int uno = 0;
            try {
               uno = Integer.parseInt(txtUno.getText());
            } catch (Exception e2) {
               JOptionPane.showMessageDialog(this, "번호는 숫자만 입력 가능합니다.");
               txtUno.requestFocus();
               return;
            }

            // 회원정보조회/고객
            try {
               conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");
               String usql = "select uno, uname, uadd from userinfo where uno=?";
               pstmt = conn.prepareStatement(usql);
               pstmt.setInt(1, uno);
               rsv1 = pstmt.executeQuery();

               while (rsv1.next()) {
                  String uinfo = rsv1.getString(1) + rsv1.getString(2) + rsv1.getString(3);
                  txtUno.setText(rsv1.getString(1));
                  txtUname.setText(rsv1.getString(2));
                  txtUadd.setText(rsv1.getString(3));

               }
            } catch (Exception e2) {
               // TODO: handle exception
            }

            // 회원정보조회/도서
            int count = 0;
            try {
               conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");

               String bsql = "SELECT bno, bname, byear \r\n" + "from userinfo \r\n"
                     + "LEFT OUTER JOIN bookinfo on bookinfo_uno = uno WHERE uno =?";
               pstmt = conn.prepareStatement(bsql);
               pstmt.setInt(1, uno);
               rsv = pstmt.executeQuery();
               vmodel.setNumRows(0);
               while (rsv.next()) {
                  String[] ubinfo = { rsv.getString(1), rsv.getString(2), rsv.getString(3) };
                  vmodel.addRow(ubinfo);
                  count += 1;
               }
               lblb.setText("대여건수 :" + count);
            } catch (Exception e2) {
               System.out.println("조회오류 : " + e2);
            }

         } else if (e.getSource() == btnEc) {
            vmodel.setNumRows(0);
            txtUno.setText("");
            txtUname.setText("");
            txtUadd.setText("");
            lblb.setText("대여건수 : 0");
            txtUno.requestFocus(); // 도서넘버로 초기화
            return;

         } else if (e.getSource() == btnEx) {
            dispose();
            System.out.println("종료");

         }
      }

   }

   class Cuser extends JDialog implements ActionListener {
      JButton btnIn = new JButton("등록");
      JButton btnCl = new JButton("취소");
      JButton btnEx = new JButton("종료");
      JLabel cou = new JLabel("등록 이용자 수 : 0");

      JTextField txtUname = new JTextField("", 10);
      JTextField txtUadd = new JTextField("", 10);

      String[][] udatas = new String[0][2];
      String[] utitles = { "이용자 번호", "이용자 이름" };

      DefaultTableModel umodel = new DefaultTableModel(udatas, utitles);
      JTable utable = new JTable(umodel);

      Connection conn;
      PreparedStatement pstmt;
      ResultSet rsn;

      public Cuser(Frame frame) {
         super(frame, "이용자 정보 등록");
         accDb();

         setModal(true);
         setResizable(true);

         setLayout(new GridLayout(6, 1));

         JPanel pn2 = new JPanel();
         pn2.add(new JLabel("이름 :", JLabel.LEFT));
         pn2.add(txtUname);
         add("North", pn2);

         JPanel pn3 = new JPanel();
         pn3.add(new JLabel("주소 :", JLabel.LEFT));
         pn3.add(txtUadd);
         add(pn3);
         JPanel pn31 = new JPanel();
         pn31.add(new JLabel("등록된 이용자 정보", JLabel.CENTER));
         add(pn31);

         table.getColumnModel().getColumn(0).setPreferredWidth(10);
         JScrollPane scl = new JScrollPane(utable);
         add("Center", scl);
         add(cou);

         JPanel pn4 = new JPanel();
         btnIn.setBackground(Color.WHITE);
         pn4.add(btnIn);
         btnCl.setBackground(Color.WHITE);
         pn4.add(btnCl);
         btnEx.setBackground(Color.WHITE);
         pn4.add(btnEx);
         btnIn.addActionListener(this);
         btnCl.addActionListener(this);
         btnEx.addActionListener(this);
         add("South", pn4);

         txtUname.setEditable(true);
         txtUadd.setEditable(true);

         setBounds(450, 450, 300, 400);
         setVisible(true);

         addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               dispose();
            }
         });

      }

      private void accDb() {
         try {
            Class.forName("org.mariadb.jdbc.Driver");

            // 자료가 실행하자마자 보여지기
            userData();
         } catch (Exception e) {
            System.out.println("accDb err : " + e);
         }
      }

      private void userData() {
         umodel.setNumRows(0);

         try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");
            String sql = "select uno, uname from userinfo";
            pstmt = conn.prepareStatement(sql);
            rsn = pstmt.executeQuery();

            int count = 0;

            while (rsn.next()) {
               String[] useri = { rsn.getString(1), rsn.getString(2) };
               umodel.addRow(useri); // 모델에 집어넣어야 화면에 뜸.
               count += 1;
            }
            cou.setText("등록 이용자 수 : " + count);
         } catch (Exception e) {
            System.out.println("userData err : " + e);
         }
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         if (e.getSource() == btnIn) { // 이용자 추가
            // 입력자료 오류 검사
            if (txtUname.getText().equals("")) {
               JOptionPane.showMessageDialog(this, "이용자 이름 입력");
               txtUname.requestFocus();
               return;
            } else if (txtUadd.getText().equals("")) {
               JOptionPane.showMessageDialog(this, "이용자 주소 입력");
               txtUadd.requestFocus();
               return;
            }

            // 등록 가능한 상태
            try {
               conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");

               // 신상 code 구하기
               int new_uno = 0;
               String sql = "select max(uno) from userinfo";
               pstmt = conn.prepareStatement(sql);
               rsn = pstmt.executeQuery();
               if (rsn.next()) {
                  new_uno = rsn.getInt(1);
               }
               sql = "insert into userinfo values(?,?,?)";
               pstmt = conn.prepareStatement(sql);
               pstmt.setInt(1, new_uno + 1);
               pstmt.setString(2, txtUname.getText().trim()); // trim은 공백을자르는 명령어
               pstmt.setString(3, txtUadd.getText().trim());

               if (pstmt.executeUpdate() > 0) {
                  JOptionPane.showMessageDialog(this, "등록 성공!!");
                  userData();
                  txtUname.setText("");
                  txtUadd.setText("");
                  txtUname.requestFocus();
                  // dispose();
               } else {
                  JOptionPane.showMessageDialog(this, "등록 실패 ㅠ");
               }

            } catch (Exception e3) {
               System.out.println("등록 실패!" + e3);
            } finally {
               try {
                  if (rs != null)
                     rs.close();
                  if (pstmt != null)
                     pstmt.close();
                  if (conn != null)
                     conn.close();
               } catch (Exception e2) {

               }
            }

         } else if (e.getSource() == btnCl) { // 입력자료 초기화
            txtUname.setText("");
            txtUadd.setText("");
            txtUname.requestFocus();

         } else if (e.getSource() == btnEx) { // 종료
            dispose();

         }

      }

   }

   class BookRt extends JDialog implements ActionListener {
      JButton brent = new JButton("대여");
      JButton breturn = new JButton("반납");

      JTextField txtUno = new JTextField("", 10);
      JTextField txtBno = new JTextField("", 10);

      Connection conn;
      PreparedStatement pstmt;
      ResultSet rsr;

      public BookRt(Frame frame) {
         super(frame, "도서 대여/반납");
         accDb();

         setModal(true);
         setResizable(true);

         setLayout(new GridLayout(3, 1));

         JPanel pn1 = new JPanel();
         pn1.add(new JLabel("이용자 번호 : ", JLabel.LEFT));
         pn1.add(txtUno);
         add("North", pn1);

         JPanel pn2 = new JPanel();
         pn2.add(new JLabel("도서 번호 : ", JLabel.LEFT));
         pn2.add(txtBno);
         add("Center", pn2);

         JPanel pn3 = new JPanel();
         brent.setBackground(Color.white);
         pn3.add(brent);
         breturn.setBackground(Color.white);
         pn3.add(breturn);
         brent.addActionListener(this);
         breturn.addActionListener(this);
         add("South", pn3);

         txtUno.setEditable(true);
         txtBno.setEditable(true);

         setBounds(450, 450, 300, 300);
         setVisible(true);

         addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               dispose();
            }
         });

      }

      private void accDb() {
         try {
            Class.forName("org.mariadb.jdbc.Driver");
         } catch (Exception e) {
            System.out.println("accDb err : " + e);
         }
      }

        @Override
        public void actionPerformed(ActionEvent e) {
           if (e.getSource() == brent) { 
                // 입력자료 오류 검사
                if (txtUno.getText().equals("")) {
                   JOptionPane.showMessageDialog(this, "이용자번호 입력");
                   txtUno.requestFocus();
                   return;
                } else if (txtBno.getText().equals("")) {
                   JOptionPane.showMessageDialog(this, "도서번호 입력");
                   txtBno.requestFocus();
                   return;
                }
                
                //대여여부 확인
                int bno = 0;
                try {
                   bno = Integer.parseInt(txtBno.getText());
                } catch (Exception e2) {
                   JOptionPane.showMessageDialog(this, "도서번호는 숫자만 입력 가능");
                   txtBno.setText("");
                }
                int uno = 0; // null 아니면 
                try {
                   conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");
                   String sql = "SELECT bookinfo_uno FROM bookinfo LEFT OUTER JOIN userinfo on bookinfo_uno = uno WHERE bno = ?";
                   pstmt = conn.prepareStatement(sql);
                   pstmt.setInt(1, bno);
                   rs = pstmt.executeQuery();
                   while(rs.next()) {
                      uno = rs.getInt(1);
                   } 
                  
                   
                   if(uno != 0) {
                      JOptionPane.showMessageDialog(this, "대여불가능.");
                      txtUno.setText("");
                      txtBno.setText("");
                      return;
                   }
     
                } catch (Exception e2) {
                   System.out.println("대여 불가능" + e2);
                   txtUno.setText("");
                   txtBno.setText("");
                   return;
                }
                
                //대여
                uno = Integer.parseInt(txtUno.getText());
                try {
                    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");
                    String sql = "UPDATE bookinfo SET  bookinfo_uno = ?  WHERE bno = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, uno); 
                    pstmt.setInt(2, bno);

                    if(pstmt.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "대여 성공!" + "\n 이용자 " + txtUno.getText() + "번이 도서" + txtBno.getText() + "번을 대여하였습니다!");
                        txtUno.setText("");
                         txtBno.setText("");
                         return;
                        }
                     else {
                        JOptionPane.showMessageDialog(this, "대여 실패!" + "\n" + "이미 다른 이용자가 대여 중 입니다!" );
                         txtBno.setText("");

                        return;
                     }
                     
                   
                    
             } catch (Exception e2) {
                System.out.println("마지막 err : " + e2);
             }
      
           }if (e.getSource() == breturn) { 
                // 입력자료 오류 검사
                if (txtUno.getText().equals("")) {
                   JOptionPane.showMessageDialog(this, "이용자번호 입력");
                   txtUno.requestFocus();
                   return;
                } else if (txtBno.getText().equals("")) {
                   JOptionPane.showMessageDialog(this, "도서번호 입력");
                   txtBno.requestFocus();
                   return;
                }
                
                int uno = 0;
                try {
                   uno = Integer.parseInt(txtUno.getText());
                } catch (Exception e2) {
                   JOptionPane.showMessageDialog(this, "이용자번호는 숫자만 입력 가능");
                   txtUno.setText("");
                }
                
                // 반납 여부 확인
                int bno = 0;// null 아니면 
                try {
                   bno = Integer.parseInt(txtBno.getText());
                } catch (Exception e2) {
                   JOptionPane.showMessageDialog(this, "도서번호는 숫자만 입력 가능");
                   txtBno.setText("");
                }
                try {
                   conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");
                   String sql = "SELECT bno FROM bookinfo LEFT OUTER JOIN userinfo on bookinfo_uno = uno WHERE bookinfo_uno = ? and bno=?";
                   pstmt = conn.prepareStatement(sql);
                   pstmt.setInt(1, uno);
                   pstmt.setInt(2, bno);
                   rs = pstmt.executeQuery();
                   while(rs.next()) {
                      bno = rs.getInt(1);
                   }
                   if(bno == 0) {
                      JOptionPane.showMessageDialog(this, "이용자" + txtUno.getText() + "번이 대여한 책이 아닙니다!.");
                      
                      txtBno.setText("");
                      return;
                   }
                   
                } catch (Exception e2) {
                   System.out.println("반납여부 불가능" + e2);
                   txtUno.setText("");
                   txtBno.setText("");
                   return;
                }
                uno = Integer.parseInt(txtUno.getText());
                bno = Integer.parseInt(txtBno.getText());
                // 반납
                try {
                   conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123");
                   String sql = "UPDATE bookinfo SET bookinfo_uno = Null WHERE bookinfo_uno=? and bno = ?";
                   pstmt = conn.prepareStatement(sql);
                   pstmt.setInt(1, uno);
                   pstmt.setInt(2, bno);
   
                    if(pstmt.executeUpdate() > 0) {
                       JOptionPane.showMessageDialog(this, "이용자" + txtUno.getText() + "번이 도서" + txtBno.getText() + "번 반납 성공!");
                       txtUno.setText("");
                        txtBno.setText("");
                        return;
                       }
                    else {
                       JOptionPane.showMessageDialog(this, "대여한 책이 아닙니다! 반납 실패!");                       
                        txtBno.setText("");
                       return;
                    }
                    
                  
                   
            } catch (Exception e2) {
               System.out.println("마지막 err : " + e2);
            }
           }
           
        }
   }
   
   public static void main(String[] args) {
      new Library();

   }

}