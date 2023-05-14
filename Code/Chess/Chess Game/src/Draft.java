/*import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class Input extends MouseAdapter {
    Board board;
    public  boolean cond=true;
    int pressedCol,pressedRow;
    int releasedCol,releasedRow;
    public static ArrayList<Piece> hardPieces=new ArrayList<>();
    public Piece pieceXY;
    int ind=0;
    public Input(Board board) throws IOException {

        this.board=board;
        this.cond=cond;
        this.ind=ind;
    }
    @Override
    public void mousePressed(MouseEvent e) {

        pressedCol=e.getX()/ board.tilesize;
        pressedRow=e.getY()/ board.tilesize;
        pieceXY= board.getPiece(pressedCol,pressedRow);

        if ( cond && Game.isTurn(pieceXY , cond)) {
            board.selectedPiece = pieceXY;
            hardPieces.add(pieceXY);
            ind++;
        }
        else if ( !cond && ( Game.isTurn(hardPieces.get(ind-1) , cond) ) ){
             board.selectedPiece = pieceXY;
             hardPieces.add(pieceXY);
             ind++;
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (board.selectedPiece != null){
            board.selectedPiece.xPos=e.getX()- board.tilesize/2;
            board.selectedPiece.yPos=e.getY()- board.tilesize/2;
            board.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
         releasedCol=e.getX()/ board.tilesize;
         releasedRow=e.getY()/ board.tilesize;
        if (board.selectedPiece != null) {
            Move move = new Move(board, board.selectedPiece, releasedCol, releasedRow);

            if (board.isValidMove(move)) {
                try {
                    board.makeMove(move);
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                board.selectedPiece.xPos = board.selectedPiece.col * board.tilesize;
                board.selectedPiece.yPos = board.selectedPiece.row * board.tilesize;
            }

            if (( releasedCol == pressedCol ) && ( releasedRow == pressedRow )) {
                cond = false;
            }
            else {
                cond=true;
            }

        }
        board.selectedPiece=null;
        board.repaint();
    }
}
*/


/*import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
public class Game {
    Player whitePlayer;
    Player blackPlayer;
    static String[] fields;
    public static boolean whiteTurn=true;
    public Game() throws IOException {
        new Frame();
        BufferedReader br = new BufferedReader(new FileReader("player.txt"));
        String line;
        String lastLine = "";
        fields = new String[0];
        while ((line = br.readLine()) != null) {
            fields = line.split(", ");
            lastLine = line;
        }
        br.close();
        whitePlayer=new Player("White",fields[1]);
        blackPlayer=new Player("Black",fields[2]);
    }

    public static boolean isTurn(Piece p , boolean cond){
        if(!cond) {
            whiteTurn = !whiteTurn;
        }
        System.out.println(whiteTurn);
        if ( p.is_white && whiteTurn) {
           whiteTurn=false;
            return true;
        }
        else if( !p.is_white && !whiteTurn ) {
           whiteTurn=true;
            return true;
        }
        return false;
    }
}*/
public class Draft {



}
