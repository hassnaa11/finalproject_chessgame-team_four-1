package Board;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import Pieces.*;
import Game.*;
import Board.*;
import Frames.*;



public class Board extends JPanel {
    public static Image[] imgs=new Image[12];
    public int tilesize=85;
    int cols=8;
    int rows=8;
    public static ArrayList<Piece> piecelist=new ArrayList<>();
    public static ArrayList<Piece> killed_pieces=new ArrayList<>();
    public static ArrayList<Piece> whitePieceList=new ArrayList<>();
    public static ArrayList<Piece> blackPieceList=new ArrayList<>();
    public Piece selectedPiece;
    public  CheckScanner checkScanner=new CheckScanner(this);
    Input input=new Input(this);
    public int enPassantTile=-1;
    public static JLabel wKilled_lapel =new JLabel();
    public static JLabel bKilled_lapel=new JLabel();
    public static JPanel wKilled_panel=new JPanel();
    public static JPanel bKilled_panel=new JPanel();
     public static int blackScore;
    public static int whiteScore;


    public Board() throws IOException {

        this.setPreferredSize(new Dimension(cols*tilesize,rows*tilesize));
        this.setVisible(true);
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        getPiecesPhotos( imgs);
        addPieces();

        //white killed panel contains white killed lapel
        wKilled_panel.setPreferredSize(new Dimension(300, 600));
        wKilled_panel.setBackground(new Color(0x4C514E));
        wKilled_panel.setLayout(new FlowLayout(FlowLayout.LEFT,40,100));

        //white killed lapel contains white killed pieces
        wKilled_lapel.setPreferredSize(new Dimension(300, 600));
        wKilled_lapel.setLayout(new GridLayout(8,2));

        ImageIcon img=new ImageIcon("src/Resources/killed_background.jpg");
        wKilled_lapel.setIcon(img);
        wKilled_panel.add(wKilled_lapel);
        wKilled_panel.setVisible(true);


        //black killed panel contains black killed lapel
        bKilled_panel.setPreferredSize(new Dimension(300, 600));
        bKilled_panel.setBackground(new Color(0x4C514E));
        bKilled_panel.setLayout(new FlowLayout(FlowLayout.RIGHT,40,100));

        //black killed lapel contains black killed pieces
        bKilled_lapel.setPreferredSize(new Dimension(300, 600));
        bKilled_lapel.setLayout(new GridLayout(8,2));
        bKilled_lapel.setIcon(img);
        bKilled_panel.add(bKilled_lapel);
        bKilled_panel.setVisible(true);

    }

    public Piece getPiece(int col,int row){
        for (Piece piece:piecelist){
            if (piece.col==col && piece.row==row){
                return piece;
            }
        }
        return null;
    }

    public void makeMove(Move move) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (move.piece.name.equals("Pawn")){
            movePawn(move);
        }else if (move.piece.name.equals("King")) {
            moveKing(move);
        }
        move.piece.col=move.newCol;
        move.piece.row=move.newRow;
        move.piece.xPos=move.newCol*tilesize;
        move.piece.yPos=move.newRow*tilesize;
        move.piece.isFirstMove=false;
        capture(move.capture);
        //Game.checkKingStatus(move);
    }
    private void moveKing(Move move){
        if (Math.abs(move.piece.col-move.newCol)==2){
            Piece rock;
            if (move.piece.col<move.newCol){
                rock=getPiece(7,move.piece.row);
                rock.col=5;
            }else {
                rock=getPiece(0,move.piece.row);
                rock.col=3;
            }
            rock.xPos=rock.col*tilesize;
        }

    }

    private void movePawn(Move move) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        //en passant
        int colorIndex = move.piece.is_white?1:-1;
        if (getTileNum(move.newCol, move.newRow) == enPassantTile){
            move.capture=getPiece(move.newCol,move.newRow+colorIndex);
        }
        if (Math.abs(move.piece.row - move.newRow) == 2){
            enPassantTile=getTileNum(move.newCol, move.newRow+colorIndex);
        }else {
            enPassantTile=-1;
        }

        // promotions
        colorIndex = move.piece.is_white? 0:7;
        if (move.newRow == colorIndex){
            promotePawn(move);
        }
    }

    private void promotePawn(Move move) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        // Create a dialog box to allow the player to choose which piece to promote the pawn to
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a piece to promote your pawn to:", "Promotion", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        // Add the chosen piece to the piecelist and remove the pawn
        if (choice == 0) {
            piecelist.add(new Queen(this, move.newCol, move.newRow, move.piece.is_white));
        } else if (choice == 1) {
            piecelist.add(new Rook(this, move.newCol, move.newRow, move.piece.is_white));
        } else if (choice == 2) {
            piecelist.add(new Bishop(this, move.newCol, move.newRow, move.piece.is_white));
        } else if (choice == 3) {
            piecelist.add(new Knight(this, move.newCol, move.newRow, move.piece.is_white));
        }
        capture(move.piece);
    }


    public boolean isValidMove(Move move){
        if(move.newCol<0 || move.newCol>7 || move.newRow<0 || move.newRow>7 ){
            return false;
        }
        if (sameTeam(move.piece, move.capture)){
            return false;
        }
        if (!move.piece.isValidMovement(move.newCol,move.newRow)){
            return false;
        }
        if (move.piece.moveCollidesWithPiece(move.newCol,move.newRow)){
            return false;
        }
        if (checkScanner.isKingInCheck( move)){
            return false;
        }
        if (checkScanner.isKingChecked(move)){
            return false;
        }

        return true;
    }
    public boolean sameTeam(Piece piece1,Piece piece2){
        if (piece1==null || piece2 ==null){
            return false;
        }
        return piece1.is_white==piece2.is_white;

    }
    public int getTileNum(int col,int row){
        return row*rows+col;
    }
    public static Piece findKing(boolean isWhite){
        for (Piece piece:piecelist){
            if (isWhite==piece.is_white && piece.name.equals("King")){
                return piece;
            }
        }
        return null;
    }


    public void capture(Piece piece) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        if (piece != null) {

            File file=new File("src/Resources/shoot.wav");
            AudioInputStream audioStream= AudioSystem.getAudioInputStream(file);
            Clip clip=AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            killed_pieces.add(piece);
            piecelist.remove(piece);
            if (piece.is_white) {
                blackScore+=50;
                switch (piece.name) {
                    case "Queen" -> {
                            Image temp_img = Board.imgs[1].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel wking_label = new JLabel(new ImageIcon(temp_img));
                            wking_label.setVisible(true);
                            wKilled_lapel.add(wking_label);
                            Frame.frame.setVisible(true);
                        }
                        case "King" -> {
                            Image temp_img = Board.imgs[0].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel wqueen_label = new JLabel(new ImageIcon(temp_img));
                            wKilled_lapel.add(wqueen_label);
                            Frame.frame.setVisible(true);
                        }
                        case "Knight" -> {
                            Image temp_img = Board.imgs[3].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel wknight_label = new JLabel(new ImageIcon(temp_img));
                            wKilled_lapel.add(wknight_label);
                            Frame.frame.setVisible(true);
                        }
                        case "Rook" -> {
                            Image temp_img = Board.imgs[4].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel wrook_label = new JLabel(new ImageIcon(temp_img));
                            wKilled_lapel.add(wrook_label);
                            Frame.frame.setVisible(true);
                        }
                        case "Bishop" -> {
                            Image temp_img = Board.imgs[2].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel wbishop_label = new JLabel(new ImageIcon(temp_img));
                            wKilled_lapel.add(wbishop_label);
                            Frame.frame.setVisible(true);
                        }
                        case "Pawn" -> {
                            Image temp_img = Board.imgs[5].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel wpawn_label = new JLabel(new ImageIcon(temp_img));
                            wKilled_lapel.add(wpawn_label);
                            Frame.frame.setVisible(true);
                        }
                    }
                } else {
                whiteScore+=50;
                    switch (piece.name) {
                        case "Queen" -> {
                            Image temp_img = Board.imgs[7].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel bking_label = new JLabel(new ImageIcon(temp_img));
                            bKilled_lapel.add(bking_label);
                            Frame.frame.setVisible(true);
                        }
                        case "King" -> {
                            Image temp_img = Board.imgs[6].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel bqueen_label = new JLabel(new ImageIcon(temp_img));
                            bKilled_lapel.add(bqueen_label);
                            Frame.frame.setVisible(true);
                        }
                        case "Knight" -> {
                            Image temp_img = Board.imgs[9].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel bknight_label = new JLabel(new ImageIcon(temp_img));
                            bKilled_lapel.add(bknight_label);
                            Frame.frame.setVisible(true);
                        }
                        case "Rook" -> {
                            Image temp_img = Board.imgs[10].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel brook_label = new JLabel(new ImageIcon(temp_img));
                            bKilled_lapel.add(brook_label);
                            Frame.frame.setVisible(true);
                        }
                        case "Bishop" -> {
                            Image temp_img = Board.imgs[8].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel bbishop_label = new JLabel(new ImageIcon(temp_img));
                            bKilled_lapel.add(bbishop_label);
                            Frame.frame.setVisible(true);
                        }
                        case "Pawn" -> {
                            Image temp_img = Board.imgs[11].getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                            JLabel bpawn_label = new JLabel(new ImageIcon(temp_img));
                            bKilled_lapel.add(bpawn_label);
                            Frame.frame.setVisible(true);
                        }
                    }
                }
            }
    }
    public void addPieces(){
        piecelist.add(new Knight(this,1,0,false));
        piecelist.add(new Knight(this,6,0,false));
        piecelist.add(new Bishop(this,2,0,false));
        piecelist.add(new Bishop(this,5,0,false));
        piecelist.add(new Rook(this,0,0,false));
        piecelist.add(new Rook(this,7,0,false));
        piecelist.add(new King(this,4,0,false));
        piecelist.add(new Queen(this,3,0,false));
        piecelist.add(new Pawn(this,0,1,false));
        piecelist.add(new Pawn(this,1,1,false));
        piecelist.add(new Pawn(this,2,1,false));
        piecelist.add(new Pawn(this,3,1,false));
        piecelist.add(new Pawn(this,4,1,false));
        piecelist.add(new Pawn(this,5,1,false));
        piecelist.add(new Pawn(this,6,1,false));
        piecelist.add(new Pawn(this,7,1,false));

        piecelist.add(new Knight(this,1,7,true));
        piecelist.add(new Knight(this,6,7,true));
        piecelist.add(new Bishop(this,2,7,true));
        piecelist.add(new Bishop(this,5,7,true));
        piecelist.add(new Rook(this,0,7,true));
        piecelist.add(new Rook(this,7,7,true));
        piecelist.add(new King(this,4,7,true));
        piecelist.add(new Queen(this,3,7,true));
        piecelist.add(new Pawn(this,0,6,true));
        piecelist.add(new Pawn(this,1,6,true));
        piecelist.add(new Pawn(this,2,6,true));
        piecelist.add(new Pawn(this,3,6,true));
        piecelist.add(new Pawn(this,4,6,true));
        piecelist.add(new Pawn(this,5,6,true));
        piecelist.add(new Pawn(this,6,6,true));
        piecelist.add(new Pawn(this,7,6,true));
    }

    public void paintComponent(Graphics g){
        Graphics2D g2d=(Graphics2D) g;

        //paint the board
        for (int r=0 ; r<rows ; r++){
            for (int c=0 ; c<cols ; c++){
                g2d.setColor((c+r)%2==0? new Color(245,255,250):new Color(0xBDB7A7)); //new Color(95, 158, 160)
                g2d.fillRect(c*tilesize,r*tilesize,tilesize,tilesize);
            }
        }

        //paint highlights
        if (selectedPiece != null) {
            for (int r=0 ; r < rows ; r++) {
                for (int c=0 ; c < cols ; c++) {
                    Move move=new Move(this, selectedPiece, c, r);
                    if (isValidMove(move)) {
                        g2d.setColor(new Color(47, 187, 65, 121));
                        g2d.fillRect(c * tilesize, r * tilesize, tilesize, tilesize);
                    }

                    if( selectedPiece.col==c && selectedPiece.row==r )   continue;

                    if(move.piece.isValidMovement(c,r) && sameTeam(move.piece,move.capture) && !move.piece.moveCollidesWithPiece(c,r)){
                        g2d.setColor(new Color(255, 8, 17, 121));
                        g2d.fillRect(c * tilesize, r * tilesize, tilesize, tilesize);
                    }
                }
            }
        }
        // paint pieces
        for (Piece piece:piecelist){
            piece.paint(g2d);
        }
    }
    // cut 12 photos for pieces
    public static void getPiecesPhotos (Image[] imgs)throws IOException {
        BufferedImage all= ImageIO.read(new File("src/Resources/chess.png"));
        int ind=0;
        for(int y=0;y<400;y+=200){
            for(int x=0;x<1200;x+=200){
                imgs[ind]=all.getSubimage(x, y, 200, 200).getScaledInstance(95, 95, BufferedImage.SCALE_SMOOTH);
                ind++;
            }
        }
    }
}
