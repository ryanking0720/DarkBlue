package com.DarkBlue.Piece;

import java.util.ArrayList;
import javax.swing.JOptionPane;

import com.DarkBlue.Player.Minimax;
import com.DarkBlue.Move.Move;
import com.DarkBlue.Move.Delta;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Factory;
import com.DarkBlue.Board.Board;
import com.DarkBlue.GUI.DarkBlue;

/*
 * This represents a chess piece that can move one tile in front if it is not blocked, 
 * or two tiles in front on its first move (given that second tile is not blocked either). 
 * It can only move one tile on its first move if the second tile ahead of it is blocked.
 * If the pawn did not move and the second tile becomes unblocked on a later turn, it regains the privilege of moving two tiles.
 * 
 * If a pawn moves two tiles on its fist move, this makes the board record the tile before it as the new en passant tile, which is used
 * in the FEN file format for determining all useful information before resuming a game.
 * Any other move by a pawn or any other piece will render this field null.
 * 
 * The pawn captures by moving diagonally forward in either direction, and can only do so if a non-king enemy piece is present on such a tile.
 * 
 * The pawn can also be promoted to a queen, rook, bishop, or knight if it reaches the opponent's innermost rank.
 * There is no limit on what pieces are on the board or were previously captured when determining promotions.
 * For example, a player could have up to nine queens or ten rooks, knights, or bishops if all of his/her pawns get promoted.
 * The effect of whichever promoted piece is chosen is immediate; a promotion could very well lead to check or checkmate.
 * 
 * The pawn can capture an enemy pawn that just moved two tiles on its first move the previous turn in a special move known as "en passant".
 * The pawn loses this right if no action is taken on that turn.
 */
public final class Pawn extends Piece{
    
    private final ArrayList<Move> m_currentRegularMoves;
    private final ArrayList<Move> m_currentAttackingMoves;
    private final ArrayList<Move> m_currentEnPassantMoves;
    
    private static final String PROMOTION = "Promote pawn to:";
    
    /**/
    /*
    NAME
        public Pawn(final ChessColor a_color, final char a_descriptor, final int a_currentRow, final int a_currentColumn);
    
    SYNOPSIS
        public Pawn(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
        
        ChessColor a_color --------> The color of the piece, used primarily by the GUI.

        int a_currentRow ----------> The piece's current row.
        
        int a_currentColumn -------> The piece's current column.
    
    DESCRIPTION
        This constructor constructs a new Pawn object by calling the Piece 
        superclass constructor and filling in the specific fields.
        
        ArrayLists that contain all and current legal moves are also instantiated, to be
        populated later.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Pawn(final ChessColor a_color, final int a_currentRow, final int a_currentColumn){

        super(a_color, a_currentRow, a_currentColumn);
        
        m_currentRegularMoves = new ArrayList<>();
        m_currentAttackingMoves = new ArrayList<>();
        m_currentEnPassantMoves = new ArrayList<>();
    }
    
    /**/
    /*
    NAME
        public Pawn(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
    
    SYNOPSIS
        public Pawn(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
        
        Piece a_piece --------> The Piece to be copied.
        
        int a_newRow ---------> The Piece's new row.
        
        int a_newColumn ------> The Piece's new column.
        
        int a_moves ----------> The Piece's new move count.
    
    DESCRIPTION
        This copy constructor constructs a new Pawn object by passing in
        a Piece object and cloning its fields.
        Row, column, and move count are passed in separately.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Pawn(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        super(a_piece, a_newRow, a_newColumn, a_moves);
        final Pawn candidate = (Pawn) a_piece;
        
        this.m_currentRegularMoves = new ArrayList<>();        
        this.m_currentAttackingMoves = new ArrayList<>();
        this.m_currentEnPassantMoves = new ArrayList<>();
        
        this.m_currentRegularMoves.addAll(candidate.GetCurrentRegularMoves());
        this.m_currentAttackingMoves.addAll(candidate.GetCurrentAttackingMoves());
        this.m_currentEnPassantMoves.addAll(candidate.GetCurrentEnPassantMoves());
    }
    
    /**/
    /*
    NAME
        public void AddCurrentLegalMoves(final Board a_board);
    
    SYNOPSIS
        public void AddCurrentLegalMoves(final Board a_board);
    
        Board a_board ---> The chessboard which contains the current game state.
    
    DESCRIPTION
        This method populates the current legal move array, taking into account which
        tiles the piece can actually visit on this turn. For example, no movement would be
        allowed to a tile if a piece is blocking it or diagonally if no non-king enemy piece exists. 
        Also, this piece may not have any legal moves if the king is in check and the piece can't help him.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public void AddCurrentLegalMoves(final Board a_board){
        // Clear the main and specific ArrayLists of moves
        this.m_currentLegalMoves.clear();
        
        this.m_currentRegularMoves.clear();
        this.m_currentAttackingMoves.clear();
        this.m_currentEnPassantMoves.clear();

        // Instantiate aliases for Delta arrays for regular and attacking move checking
        final Delta[] regularMoves, attackingMoves;
        
        // Determine the proper arrays to check,
        // since a pawn's direction is based on its color
        if(this.GetColor().IsWhite()){
            regularMoves = MoveEvaluation.m_allWhiteRegularMoves;
            attackingMoves = MoveEvaluation.m_allWhiteAttackingMoves;
        }else{
            regularMoves = MoveEvaluation.m_allBlackRegularMoves;
            attackingMoves = MoveEvaluation.m_allBlackAttackingMoves;
        }
        
        // Add the pawn's regular and attacking moves, if any
        this.m_currentRegularMoves.addAll(MoveEvaluation.AddCurrentRegularMoves(this, a_board, regularMoves));
        this.m_currentAttackingMoves.addAll(MoveEvaluation.AddCurrentAttackingMoves(this, a_board, attackingMoves));
        
        // Determine if the pawn is on its fifth rank to add en passant moves
        if((this.IsWhite() && this.GetCurrentRow() == Utilities.THREE) 
                || (this.IsBlack() && this.GetCurrentRow() == Utilities.FOUR)){
            this.m_currentEnPassantMoves.addAll(MoveEvaluation.AddCurrentEnPassantMoves(this, a_board));
        }
        
        // Add all of the previously calculated moves to the current legal moves ArrayList
        this.m_currentLegalMoves.addAll(m_currentRegularMoves);

        this.m_currentLegalMoves.addAll(m_currentAttackingMoves);

        this.m_currentLegalMoves.addAll(m_currentEnPassantMoves);
    }
    
    /**/
    /*
    NAME
        public final PieceType GetPieceType();
    
    SYNOPSIS
        public final PieceType GetPieceType();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's type.
    
    RETURNS
        PieceType.PAWN.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final PieceType GetPieceType(){
        return PieceType.PAWN;
    }
    
    /**/
    /*
    NAME
        public final char GetIcon();
    
    SYNOPSIS
        public final char GetIcon();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's algebraic notation icon.
    
    RETURNS
        char: This piece's algebraic notation icon.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final char GetIcon(){
        if(this.IsWhite()){
            return Utilities.WHITE_PAWN_ICON;
        }else{
            return Utilities.BLACK_PAWN_ICON;
        }
    }
    
    /**/
    /*
    NAME
        public final char GetBoardIcon();
    
    SYNOPSIS
        public final char GetBoardIcon();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's board icon.
    
    RETURNS
        char: This piece's board icon.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final char GetBoardIcon(){
        if(this.IsWhite()){
            return Utilities.WHITE_PAWN_BOARD_ICON;
        }else{
            return Utilities.BLACK_PAWN_BOARD_ICON;
        }
    }
    
    /**/
    /*
    NAME
        public final boolean IsPawn();
    
    SYNOPSIS
        public final boolean IsPawn();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a pawn.
    
    RETURNS
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsPawn(){
        return true;
    }
    
    /**/
    /*
    NAME
        public final boolean IsKing();
    
    SYNOPSIS
        public final boolean IsKing();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a king.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsKing(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsRook();
    
    SYNOPSIS
        public final boolean IsRook();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a rook.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsRook(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsBishop();
    
    SYNOPSIS
        public final boolean IsBishop();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a bishop.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsBishop(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsQueen();
    
    SYNOPSIS
        public final boolean IsQueen();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a queen.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsQueen(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsKnight();
    
    SYNOPSIS
        public final boolean IsKnight();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a knight.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsKnight(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentRegularMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentRegularMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of regular moves.
    
    RETURNS
        m_currentRegularMoves: An ArrayList containing this pawn's regular moves
        for the current turn.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentRegularMoves(){
        return this.m_currentRegularMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentAttackingMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentAttackingMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of attacking moves.
    
    RETURNS
        m_currentAttackingMoves: An ArrayList containing this pawn's attacking moves
        for the current turn.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentAttackingMoves(){
        return this.m_currentAttackingMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentEnPassantMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentEnPassantMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of en passant moves.
    
    RETURNS
        m_currentEnPassantMoves: An ArrayList containing this pawn's en passant moves
        for the current turn.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentEnPassantMoves(){
        return this.m_currentEnPassantMoves;
    }

    /**/
    /*
    NAME
        public final Board Promote(final Board a_board, final boolean a_isHuman);
    
    SYNOPSIS
        public final Board Promote(final Board a_board, final boolean a_isHuman);
    
        Board a_board ------------> The board where a pawn is going to be promoted.
        
        boolean a_isHuman --------> A flag indicating whether the player is human or not.
    
    DESCRIPTION
        This method promotes a pawn that has reached the farthest possible rank.
        A human player will have the choice of converting it to a new queen, rook,
        bishop, or knight by using a set of 4 buttons.
        A computer player will choose the best option to instantiate the desired piece.
        Either type of player will have the choice of converting it to any piece,
        no matter how many of those pieces currently exist on the board.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final Board Promote(final Board a_board, final boolean a_isHuman){
        // Initialize options we'll need for buttons
        final Object[] options = {"Queen", "Rook", "Bishop", "Knight"};
        
        // Data we'll need to instantiate the piece
        final Piece newPiece;
        
        // Keep looping until the user chooses an option.
        // If s/he closes out, start up again.
        int buttonInt;
        
        final ArrayList<Double> values = new ArrayList<>();
        final ChessColor color = this.GetColor();
        final int row = this.GetCurrentRow();
        final int column = this.GetCurrentColumn();
        
        if(a_isHuman){
        
            while(true){
            
                // Determine which piece the user wants to promote this pawn to
                buttonInt = JOptionPane.showOptionDialog(null, PROMOTION, DarkBlue.TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

                // Only break out if the user chose a piece
                if(buttonInt != JOptionPane.CLOSED_OPTION){
                    break;
                }
            }
        
        }else{           
            
            for(int i = Utilities.ZERO; i < Utilities.FOUR; i++){
                Board clone = Board.GetDeepCopy(a_board);
                clone = clone.Promote(Factory.PromotedPieceFactory(color, row, column, i));
                values.add((color.IsWhite() ? Minimax.Evaluate(clone, color) : -Minimax.Evaluate(clone, color)));
            }
            
            buttonInt = (color.IsWhite() ? GetSmallestIndex(values) : GetLargestIndex(values));
        }
        
        newPiece = Factory.PromotedPieceFactory(color, row, column, buttonInt);
        
        // Return the board with the newly-promoted piece
        return a_board.Promote(newPiece);
    }
    
    private final int GetLargestIndex(final ArrayList<Double> a_values){
        int largest = Utilities.ZERO;
        
        for(int i = Utilities.ONE; i < a_values.size(); i++){
            if(a_values.get(i) > a_values.get(largest)){
                largest = i;
            }
        }
        
        return largest;
    }
    
    public final Board Promote(final Board a_board, final int a_type){
        Board clone = Board.GetDeepCopy(a_board);
        return clone.Promote(Factory.PromotedPieceFactory(this.m_color, this.m_currentRow, this.m_currentColumn, a_type));
    }
    
    private final int GetSmallestIndex(final ArrayList<Double> a_values){
        int smallest = Utilities.ZERO;
        
        for(int i = Utilities.ONE; i < a_values.size(); i++){
            if(a_values.get(i) < a_values.get(smallest)){
                smallest = i;
            }
        }
        
        return smallest;
    }
}