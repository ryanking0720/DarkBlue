package com.DarkBlue.Piece;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

import com.DarkBlue.Move.*;
import com.DarkBlue.Board.*;
import com.DarkBlue.GUI.DarkBlue;
import com.DarkBlue.Utilities.*;

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

        super(a_color, PieceType.PAWN, Utilities.WHITE_PAWN_ICON, AssignPieceBoardIcon(PieceType.PAWN, a_color), a_currentRow, a_currentColumn, AssignPieceValue(PieceType.PAWN, a_color));
        
        m_currentRegularMoves = new ArrayList<>();
        m_currentAttackingMoves = new ArrayList<>();
        m_currentEnPassantMoves = new ArrayList<>();
    }
    
    /**/
    /*
    NAME
        public Pawn(final Piece a_piece);
    
    SYNOPSIS
        public Pawn(final Piece a_piece);
        
        Piece a_piece --------> The Piece to be copied.
    
    DESCRIPTION
        This copy constructor constructs a new Pawn object by passing in
        a Piece object and cloning its fields.
        
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
        
        this.m_currentRegularMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentRegularMoves()));
        this.m_currentAttackingMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentAttackingMoves()));
        this.m_currentEnPassantMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentEnPassantMoves()));
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
        tiles the piece can actually visit on this turn. For example, no tile occurring after an
        opposing piece or on and after a friendly piece can be visited. Also, this piece
        may not have any legal moves if the king is in check and the piece can't help him.
    
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
        
        this.m_attackedTiles.clear();
        
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
        public ArrayList<Move> GetCurrentRegularMoves();
    
    SYNOPSIS
        public ArrayList<Move> GetCurrentRegularMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of regular moves.
    
    RETURNS
        m_currentEnPassantMoves: An ArrayList containing this pawn's regular moves
        for the current turn.
    
    AUTHOR
        Ryan King
    */
    public ArrayList<Move> GetCurrentRegularMoves(){
        return this.m_currentRegularMoves;
    }
    
    /**/
    /*
    NAME
        public ArrayList<Move> GetCurrentAttackingMoves();
    
    SYNOPSIS
        public ArrayList<Move> GetCurrentAttackingMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of attacking moves.
    
    RETURNS
        m_currentAttackingMoves: An ArrayList containing this pawn's attacking moves
        for the current turn.
    
    AUTHOR
        Ryan King
    */
    public ArrayList<Move> GetCurrentAttackingMoves(){
        return this.m_currentAttackingMoves;
    }
    
    /**/
    /*
    NAME
        public ArrayList<Move> GetCurrentEnPassantMoves();
    
    SYNOPSIS
        public ArrayList<Move> GetCurrentEnPassantMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of en passant moves.
    
    RETURNS
        m_currentEnPassantMoves: An ArrayList containing this pawn's en passant moves
        for the current turn.
    
    AUTHOR
        Ryan King
    */
    public ArrayList<Move> GetCurrentEnPassantMoves(){
        return this.m_currentEnPassantMoves;
    }

    /**/
    /*
    NAME
        public Board Promote(final Board a_board, final boolean a_isHuman);
    
    SYNOPSIS
        public Board Promote(final Board a_board, final boolean a_isHuman);
    
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
    public Board Promote(final Board a_board, final boolean a_isHuman){
        // Initialize options we'll need for buttons
        Object[] options = {"Queen", "Rook", "Bishop", "Knight"};
        
        // Data we'll need to instantiate the piece
        final Piece newPiece;
        
        // Keep looping until the user chooses an option.
        // If s/he closes out, start up again.
        int buttonInt;
        
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
        	
        	// Again, make another stupid AI for now
        	Random random = new Random();
        	buttonInt = random.nextInt(Utilities.FOUR);
        }
        
        // Instantiate the chosen piece
    	switch(buttonInt){
        	case Utilities.ZERO: newPiece = new Queen(this.GetColor(), this.GetCurrentRow(), this.GetCurrentColumn());
        	break;
        	case Utilities.ONE: newPiece = new Rook(this.GetColor(), this.GetCurrentRow(), this.GetCurrentColumn());
        	break;
        	case Utilities.TWO: newPiece = new Bishop(this.GetColor(), this.GetCurrentRow(), this.GetCurrentColumn());
        	break;
        	case Utilities.THREE: newPiece = new Knight(this.GetColor(), this.GetCurrentRow(), this.GetCurrentColumn());
        	break;
        	default: newPiece = null;
        	break;
    	}
        
        // Return the board with the newly-promoted piece
        return a_board.Promote(newPiece);
    }
}