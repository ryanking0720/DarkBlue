package com.DarkBlue.Utilities;

import java.util.ArrayList;

import com.DarkBlue.Board.Board;
import com.DarkBlue.GUI.DarkBlue;
import com.DarkBlue.Move.*;
import com.DarkBlue.Piece.*;
import com.DarkBlue.Player.*;
/* 
 * This interface is used to evaluate move legality
 * and determine king safety. It contains arrays of 
 * moves that can be made by all pieces. These are used
 * by the respective classes when their AddCurrentLegalMoves()
 * methods are called.
 * 
 * Rooks, bishops, and queens use the AddCurrentDirectionalMoves() method
 * for evaluating move legality because once an illegal move is detected, no other
 * move after it is legal.
 * 
 * Kings and knights use the AddCurrentSpectrumMoves() method for evaluating move
 * legality because all spots must be checked every time in sequence.
 * 
 * Pawns use the AddCurrentRegularMoves() and AddCurrentAttackingMoves() methods
 * because they move and attack differently. They can also use AddCurrentEnPassantMoves()
 * if they are on their fifth rank, though it will not always lead to any moves getting added.
 * Black and white pawns have their own arrays for these moves because they can only move in one direction.
 * 
 * This also contains minimax evaluation grids for the computer to use to evaluate position and material.
 */
public interface MoveEvaluation{
    
    // All king moves that can be made on any turn
    public static final Delta[] m_allKingMoves = {
        new Delta(Utilities.NEGATIVE_ONE, Utilities.NEGATIVE_ONE),
        new Delta(Utilities.NEGATIVE_ONE, Utilities.ZERO),
        new Delta(Utilities.NEGATIVE_ONE, Utilities.ONE),
        new Delta(Utilities.ZERO, Utilities.ONE),
        new Delta(Utilities.ONE, Utilities.ONE),
        new Delta(Utilities.ONE, Utilities.ZERO),
        new Delta(Utilities.ONE, Utilities.NEGATIVE_ONE),
        new Delta(Utilities.ZERO, Utilities.NEGATIVE_ONE)
    };
    
    // All knight moves that can be made on any turn
    public static final Delta[] m_allKnightMoves = {
        new Delta(Utilities.NEGATIVE_ONE, Utilities.NEGATIVE_TWO),
        new Delta(Utilities.NEGATIVE_TWO, Utilities.ONE),
        new Delta(Utilities.NEGATIVE_ONE, Utilities.TWO),
        new Delta(Utilities.ONE, Utilities.TWO),
        new Delta(Utilities.TWO, Utilities.ONE),
        new Delta(Utilities.TWO, Utilities.NEGATIVE_ONE),
        new Delta(Utilities.ONE, Utilities.NEGATIVE_TWO),
        new Delta(Utilities.NEGATIVE_TWO, Utilities.NEGATIVE_ONE)
    };
    
    // All down moves that can be made any turn
    public static final Delta[] m_allDownMoves = {
        new Delta(Utilities.ONE, Utilities.ZERO),
        new Delta(Utilities.TWO, Utilities.ZERO),
        new Delta(Utilities.THREE, Utilities.ZERO),
        new Delta(Utilities.FOUR, Utilities.ZERO),
        new Delta(Utilities.FIVE, Utilities.ZERO),
        new Delta(Utilities.SIX, Utilities.ZERO),
        new Delta(Utilities.SEVEN, Utilities.ZERO)
    };
            
    // All up moves that can be made any turn
    public static final Delta[] m_allUpMoves = {
        new Delta(Utilities.NEGATIVE_ONE, Utilities.ZERO),
        new Delta(Utilities.NEGATIVE_TWO, Utilities.ZERO),
        new Delta(Utilities.NEGATIVE_THREE, Utilities.ZERO),
        new Delta(Utilities.NEGATIVE_FOUR, Utilities.ZERO),
        new Delta(Utilities.NEGATIVE_FIVE, Utilities.ZERO),
        new Delta(Utilities.NEGATIVE_SIX, Utilities.ZERO),
        new Delta(Utilities.NEGATIVE_SEVEN, Utilities.ZERO)
    };
            
    // All right moves that can be made any turn
    public static final Delta[] m_allRightMoves = {
        new Delta(Utilities.ZERO, Utilities.ONE),
        new Delta(Utilities.ZERO, Utilities.TWO),
        new Delta(Utilities.ZERO, Utilities.THREE),
        new Delta(Utilities.ZERO, Utilities.FOUR),
        new Delta(Utilities.ZERO, Utilities.FIVE),
        new Delta(Utilities.ZERO, Utilities.SIX),
        new Delta(Utilities.ZERO, Utilities.SEVEN)
    };
            
    // All left moves that can be made any turn
    public static final Delta[] m_allLeftMoves = {
        new Delta(Utilities.ZERO, Utilities.NEGATIVE_ONE),
        new Delta(Utilities.ZERO, Utilities.NEGATIVE_TWO),
        new Delta(Utilities.ZERO, Utilities.NEGATIVE_THREE),
        new Delta(Utilities.ZERO, Utilities.NEGATIVE_FOUR),
        new Delta(Utilities.ZERO, Utilities.NEGATIVE_FIVE),
        new Delta(Utilities.ZERO, Utilities.NEGATIVE_SIX),
        new Delta(Utilities.ZERO, Utilities.NEGATIVE_SEVEN)
    };    
        
    // All down and right moves that can be made any turn
    public static final Delta[] m_allDownAndRightMoves = {
        new Delta(Utilities.ONE, Utilities.ONE),
        new Delta(Utilities.TWO, Utilities.TWO),
        new Delta(Utilities.THREE, Utilities.THREE),
        new Delta(Utilities.FOUR, Utilities.FOUR),
        new Delta(Utilities.FIVE, Utilities.FIVE),
        new Delta(Utilities.SIX, Utilities.SIX),
        new Delta(Utilities.SEVEN, Utilities.SEVEN)
    };
            
    // All up and right moves that can be made any turn
    public static final Delta[] m_allUpAndRightMoves = {
        new Delta(Utilities.NEGATIVE_ONE, Utilities.ONE),
        new Delta(Utilities.NEGATIVE_TWO, Utilities.TWO),
        new Delta(Utilities.NEGATIVE_THREE, Utilities.THREE),
        new Delta(Utilities.NEGATIVE_FOUR, Utilities.FOUR),
        new Delta(Utilities.NEGATIVE_FIVE, Utilities.FIVE),
        new Delta(Utilities.NEGATIVE_SIX, Utilities.SIX),
        new Delta(Utilities.NEGATIVE_SEVEN, Utilities.SEVEN)
    };
            
    // All down and left moves that can be made any turn
    public static final Delta[] m_allUpAndLeftMoves = {
        new Delta(Utilities.NEGATIVE_ONE, Utilities.NEGATIVE_ONE),
        new Delta(Utilities.NEGATIVE_TWO, Utilities.NEGATIVE_TWO),
        new Delta(Utilities.NEGATIVE_THREE, Utilities.NEGATIVE_THREE),
        new Delta(Utilities.NEGATIVE_FOUR, Utilities.NEGATIVE_FOUR),
        new Delta(Utilities.NEGATIVE_FIVE, Utilities.NEGATIVE_FIVE),
        new Delta(Utilities.NEGATIVE_SIX, Utilities.NEGATIVE_SIX),
        new Delta(Utilities.NEGATIVE_SEVEN, Utilities.NEGATIVE_SEVEN)
    };
            
    // All down and left moves that can be made any turn
    public static final Delta[] m_allDownAndLeftMoves = {
        new Delta(Utilities.ONE, Utilities.NEGATIVE_ONE),
        new Delta(Utilities.TWO, Utilities.NEGATIVE_TWO),
        new Delta(Utilities.THREE, Utilities.NEGATIVE_THREE),
        new Delta(Utilities.FOUR, Utilities.NEGATIVE_FOUR),
        new Delta(Utilities.FIVE, Utilities.NEGATIVE_FIVE),
        new Delta(Utilities.SIX, Utilities.NEGATIVE_SIX),
        new Delta(Utilities.SEVEN, Utilities.NEGATIVE_SEVEN)
    };
    
    // Both regular moves a white pawn can make
    public static final Delta[] m_allWhiteRegularMoves = {
        new Delta(Utilities.NEGATIVE_ONE, Utilities.ZERO),
        new Delta(Utilities.NEGATIVE_TWO, Utilities.ZERO)
    };
    
    // Both attacking moves a white pawn can make
    public static final Delta[] m_allWhiteAttackingMoves = {
        new Delta(Utilities.NEGATIVE_ONE, Utilities.NEGATIVE_ONE),
        new Delta(Utilities.NEGATIVE_ONE, Utilities.ONE)
    };
        
    // Both regular moves a black pawn can make
    public static final Delta[] m_allBlackRegularMoves = {
        new Delta(Utilities.ONE, Utilities.ZERO),
        new Delta(Utilities.TWO, Utilities.ZERO)
    };
    
    // Both attacking moves a black pawn can make
    public static final Delta[] m_allBlackAttackingMoves = {
        new Delta(Utilities.ONE, Utilities.NEGATIVE_ONE),
        new Delta(Utilities.ONE, Utilities.ONE)
    };
    
    // Both en passant moves a pawn can make
    public static final Delta[] m_allEnPassantMoves = {
        new Delta(Utilities.ZERO, Utilities.ONE),
        new Delta(Utilities.ZERO, Utilities.NEGATIVE_ONE)
    };
    
    // Minimax evaluation arrays for all pieces
    // Source for all arrays: https://jsfiddle.net/q76uzxwe/1/
    public static final double[][] m_whiteKingPositions = {
    	{-3, -4, -4, -5, -5, -4, -4, -3},
    	{-3, -4, -4, -5, -5, -4, -4, -3},
    	{-3, -4, -4, -5, -5, -4, -4, -3},
    	{-3, -4, -4, -5, -5, -4, -4, -3},
    	{-2, -3, -3, -4, -4, -3, -3, -2},
    	{-1, -2, -2, -2, -2, -2, -2, -1},
    	{2, 0, 0, 0, 0, 0, 0, 2},
    	{2, 3, 1, 0, 0, 1, 2, 3}
    };
    
    public static final double[][] m_blackKingPositions = {
    	{2, 3, 1, 0, 0, 1, 2, 3},
    	{2, 0, 0, 0, 0, 0, 0, 2},
    	{-1, -2, -2, -2, -2, -2, -2, -1},
    	{-2, -3, -3, -4, -4, -3, -3, -2},
    	{-3, -4, -4, -5, -5, -4, -4, -3},
        {-3, -4, -4, -5, -5, -4, -4, -3},
        {-3, -4, -4, -5, -5, -4, -4, -3},
        {-3, -4, -4, -5, -5, -4, -4, -3}
    };
    
    public static final double[][] m_queenPositions = {
    	{-2, -1, -1, -0.5, -0.5, -1, -1, -2},
    	{-1, 0, 0, 0, 0, 0, 0, -1},
    	{-1, 0, -0.5, -0.5, -0.5, -0.5, 0, -1},
    	{-0.5, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5},
    	{-0.5, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5},
    	{-1, 0, -0.5, -0.5, -0.5, -0.5, 0, -1},
    	{-1, 0, 0, 0, 0, 0, 0, -1},
    	{-2, -1, -1, -0.5, -0.5, -1, -1, -2}
    };
 
    public static final double[][] m_whiteRookPositions = {
    	{0, 0, 0, 0, 0, 0, 0, 0},
    	{-0.5, 1, 1, 1, 1, 1, 1, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{0, 0, 0, 0, 0, 0, 0, 0}
    };
    
    public static final double[][] m_blackRookPositions = {
    	{0, 0, 0, 0, 0, 0, 0, 0},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    	{-0.5, 1, 1, 1, 1, 1, 1, -0.5},
    	{0, 0, 0, 0, 0, 0, 0, 0}
    };
    
    public static final double[][] m_whiteBishopPositions = {
    	{-2, -1, -1, -1, -1, -1, -1, -2},
    	{-1, 0, 0, 0, 0, 0, 0, -1},
    	{-1, 0, 0.5, 1, 1, 0.5, 0, -1},
    	{-1, 0.5, 0.5, 1, 1, 0.5, 0.5, -1},
    	{-1, 0, 1, 1, 1, 1, 0, -1},
    	{-1, 1, 1, 1, 1, 1, 1, -1},
    	{-1, 0.5, 0, 0, 0, 0, 0.5, -1},
    	{-2, -1, -1, -1, -1, -1, -1, -2}
    };
    
    public static final double[][] m_blackBishopPositions = {
    	{-2, -1, -1, -1, -1, -1, -1, -2},
    	{-1, 0.5, 0, 0, 0, 0, 0.5, -1},
    	{-1, 1, 1, 1, 1, 1, 1, -1},
    	{-1, 0, 1, 1, 1, 1, 0, -1},
    	{-1, 0.5, 0.5, 1, 1, 0.5, 0.5, -1},
    	{-1, 0, 0.5, 1, 1, 0.5, 0, -1},
    	{-1, 0, 0, 0, 0, 0, 0, -1},
    	{-2, -1, -1, -1, -1, -1, -1, -2}
    };
    
    public static final double[][] m_knightPositions = {
    	{-5, -4, -3, -3, -3, -3, -4, -5},
    	{-4, -2, 0, 0, 0, 0, -2, -4},
    	{-3, 0, 1, 1.5, 1.5, 1, 0, -3},
    	{-3, 0.5, 1.5, 2, 2, 1.5, 0.5, -3},
    	{-3, 0, 1.5, 2, 2, 1.5, 0, -3},
    	{-3, 0.5, 1, 1.5, 1.5, 1, 0.5, -3},
    	{-4, -2, 0, 0.5, 0.5, 0, -2, -4},
    	{-5, -4, -3, -3, -3, -3, -4, -5}
    };
    
    public static final double[][] m_whitePawnPositions = {
    	{0, 0, 0, 0, 0, 0, 0, 0},
    	{5, 5, 5, 5, 5, 5, 5, 5},
    	{1, 1, 2, 3, 3, 2, 1, 1},
    	{0.5, 0.5, 1, 2.5, 2.5, 1, 0.5, 0.5},
    	{0, 0, 0, 2, 2, 0, 0, 0},
    	{0.5, -0.5, -1, 0, 0, -1, -0.5, 0.5},
    	{0.5, 1, 1, -2, -2, 1, 1, 0.5},
    	{0, 0, 0, 0, 0, 0, 0, 0}
    };
    
    public static final double[][] m_blackPawnPositions = {
    	{0, 0, 0, 0, 0, 0, 0, 0},
    	{0.5, 1, 1, -2, -2, 1, 1, 0.5},
    	{0.5, -0.5, -1, 0, 0, -1, -0.5, 0.5},
    	{0, 0, 0, 2, 2, 0, 0, 0},
    	{0.5, 0.5, 1, 2.5, 2.5, 1, 0.5, 0.5},
    	{1, 1, 2, 3, 3, 2, 1, 1},
    	{5, 5, 5, 5, 5, 5, 5, 5},
    	{0, 0, 0, 0, 0, 0, 0, 0}
    };
    
    /**/
    /*
    NAME
        public static ArrayList<Move> AddCurrentDirectionalMoves(final Piece a_piece, final Board a_board, final Delta[] a_allDirectionalMoves);

    SYNOPSIS
        public static ArrayList<Move> AddCurrentDirectionalMoves(final Piece a_piece, final Board a_board, final Delta[] a_allDirectionalMoves);
    
        Piece a_piece ------------------> The piece to be evaluated.
    
        Board a_board ------------------> The chessboard on which the game is being played.
        
        Delta[] a_allDirectionalMoves --> All possible moves in this direction.

    DESCRIPTION
        This method determines the legal moves available in one direction.
        This can be up, down, left, right, up and left, up and right, down and left,
        or down and right.
        Note that the ArrayLists for these directions contain Move objects rather than
        Delta objects because these are being performed to and from a specific square, possibly with
        a specific victim piece. Any move that is otherwise valid will not be added if
        the side's king is threatened because of it.
    
    RETURNS
        ArrayList<Move> currentDirectionalMoves: The moves currently legal in this direction.
    
    AUTHOR
        Ryan King
    */
    public static ArrayList<Move> AddCurrentDirectionalMoves(final Piece a_piece, final Board a_board, final Delta[] a_allDirectionalMoves){
        int index = Utilities.ZERO, newRow = Utilities.ZERO, newColumn = Utilities.ZERO;
        ArrayList<Move> currentDirectionalMoves = new ArrayList<>();
        while(index < a_allDirectionalMoves.length){
            
            // Initialize a deep copy of the players and their board
            Board clone = null;
            Human tempWhite = new Human(ChessColor.WHITE, a_board);
            Human tempBlack = new Human(ChessColor.BLACK, a_board);
            
            try{
            	tempWhite.InitializePieces(a_board);
            	tempBlack.InitializePieces(a_board);
            }catch(Exception e){
            	e.printStackTrace();
            }
            
            // Assign an alias to the moving player
            final Player MOVER;
            
            if(a_piece.IsWhite()){
                MOVER = tempWhite;
            }else{
                MOVER = tempBlack;
            }

            // Find the destination coordinates
            newRow = a_piece.GetCurrentRow() + a_allDirectionalMoves[index].GetRowDelta();
            newColumn = a_piece.GetCurrentColumn() + a_allDirectionalMoves[index].GetColumnDelta();
            if(BoardUtilities.HasValidCoordinates(newRow, newColumn)){

                // Determine what type of move this is
                Move candidate;
                if(a_board.GetTile(newRow, newColumn).IsEmpty()){
                    candidate = new RegularMove(a_piece, newRow, newColumn, a_board);

                    clone = candidate.GetTransitionalBoard();
                    try{
                    	tempWhite.InitializePieces(clone);
                    	tempBlack.InitializePieces(clone);
                    }catch(Exception e){
                    	e.printStackTrace();
                    }

                    // Only add this move if it is safe for this side's king
                    if(MoveEvaluation.IsKingSafe(clone, MOVER.GetKing().GetCurrentRow(), MOVER.GetKing().GetCurrentColumn(), a_piece.GetColor())){
                        currentDirectionalMoves.add(candidate);
                    }
                }else{
                    final Piece VICTIM = a_board.GetTile(newRow, newColumn).GetPiece();
                    if(a_board.GetTile(newRow, newColumn).IsOccupied() && VICTIM.IsEnemy(a_piece) && !VICTIM.IsKing()){
                        // Only add this last move and no more; this is as far as the piece can go in this direction
                        candidate = new AttackingMove(a_piece, newRow, newColumn, a_board);
                        
                        clone = candidate.GetTransitionalBoard();
                        try {
                        	tempWhite.InitializePieces(clone);
                        	tempBlack.InitializePieces(clone);
                        }catch(Exception e){
                        	e.printStackTrace();
                        }

                        // Only add if safe
                        if(MoveEvaluation.IsKingSafe(clone, MOVER.GetKing().GetCurrentRow(), MOVER.GetKing().GetCurrentColumn(), a_piece.GetColor())){
                            currentDirectionalMoves.add(candidate);
                        }
                    }
                    return currentDirectionalMoves;
                }
                index++;
            }else{
                return currentDirectionalMoves;
            }
        }
        return currentDirectionalMoves;
    }
    
    /**/
    /*
    NAME
        public static ArrayList<Move> AddCurrentSpectrumMoves(final Piece a_piece, final Board a_board, final Delta[] a_allSpectrumMoves);
    
    SYNOPSIS
        public static ArrayList<Move> AddCurrentSpectrumMoves(final Piece a_piece, final Board a_board, final Delta[] a_allSpectrumMoves);
    
        Piece a_piece ------------------> The piece to be evaluated.
    
        Board a_board ------------------> The chessboard on which the game is being played.
        
        Delta[] a_allSpectrumMoves -----> All possible moves in this spectrum.

    DESCRIPTION
        This method determines the legal moves available in this spectrum.
        This means that the moves being evaluated occur nonlinearly.
        For example, kings are only allowed to move one square in any direction
        that does not result in them being threatened.
        Likewise, a knight can only move two tiles in one direction and one tile
        in a perpendicular direction or vice versa.
        Since these moves do not occur linearly, we must iterate through each and every one.
        Just because one tile is not valid does not mean another valid tile is not right next to it.
        Any move that is otherwise valid will not be added if
        the side's king is threatened because of it.
    
    RETURNS
        ArrayList<Move> currentDirectionalMoves: The moves currently legal in this direction.
    
    AUTHOR
        Ryan King
    */
    public static ArrayList<Move> AddCurrentSpectrumMoves(final Piece a_piece, final Board a_board, final Delta[] a_allSpectrumMoves){
        // Declare variables to keep track of the current tile
        int newRow, newColumn;
        
        // Declare an ArrayList to hold the legal moves
        ArrayList<Move> currentSpectrumMoves = new ArrayList<>();
        
        // Iterate through all of the spectrum moves
        for(int index = Utilities.ZERO; index < a_allSpectrumMoves.length; index++){
            
            // Make a deep copy of the current board and players
            Board clone = null;
            Human tempWhite = new Human(ChessColor.WHITE, a_board);
            Human tempBlack = new Human(ChessColor.BLACK, a_board);
            
            // Find all pieces for both sides
            try{
            	tempWhite.InitializePieces(a_board);
            	tempBlack.InitializePieces(a_board);
            }catch(Exception e){
            	e.printStackTrace();
            }
            
            // Assign an alias to the moving player
            final Player MOVER;
            
            if(a_piece.IsWhite()){
                MOVER = tempWhite;
            }else{
                MOVER = tempBlack;
            }
            
            // Find the destination tile using the piece's current coordinates and the deltas
            newRow = a_piece.GetCurrentRow() + a_allSpectrumMoves[index].GetRowDelta();
            newColumn = a_piece.GetCurrentColumn() + a_allSpectrumMoves[index].GetColumnDelta();
            
            // Do not continue evaluating this move if the destination tile is invalid
            if(BoardUtilities.HasValidCoordinates(newRow, newColumn)){

                // Instantiate a candidate move (This could be turned into a regular or attacking move)
                Move candidate;
                
                // If the tile is empty, this move is a regular move
                if(a_board.GetTile(newRow, newColumn).IsEmpty()){
                    
                    // Instantiate the candidate move as a member of the RegularMove subclass
                    candidate = new RegularMove(a_piece, newRow, newColumn, a_board);
                    
                    // Make the move on the deep copy of the board (This may not be a legal move)
                    clone = candidate.GetTransitionalBoard();
                    
                    try{
                    	tempWhite.InitializePieces(clone);
                    	tempBlack.InitializePieces(clone);
                    }catch(Exception e){
                    	e.printStackTrace();
                    }
                    
                    // Do not add the move if this side's king is not safe
                    if(MoveEvaluation.IsKingSafe(clone, MOVER.GetKing().GetCurrentRow(), MOVER.GetKing().GetCurrentColumn(), a_piece.GetColor())){ // bp
                        currentSpectrumMoves.add(candidate);
                    }// Make the method "mover" field a certain color based off the piece being evaluated, not the side whose turn it is?
                    
                    clone = null;
                    tempWhite = null;
                    tempBlack = null;
                    candidate = null;
                    
                }else{
                    // Find the victim of this attacking move
                    final Piece VICTIM = a_board.GetTile(newRow, newColumn).GetPiece();
                    
                    // Only allow further evaluation if the victim is an enemy piece that is not the king
                    if(a_board.GetTile(newRow, newColumn).IsOccupied() && VICTIM.IsEnemy(a_piece) && !VICTIM.IsKing()){
                        candidate = new AttackingMove(a_piece, newRow, newColumn, a_board);
                        
                        // Make the move
                        clone = candidate.GetTransitionalBoard();
                        
                        try{
                        	tempWhite.InitializePieces(clone);
                        	tempBlack.InitializePieces(clone);
                        }catch(Exception e){
                        	e.printStackTrace();
                        }
                        
                        // Determine if this move is safe. If so, add it.
                        if(MoveEvaluation.IsKingSafe(clone, MOVER.GetKing().GetCurrentRow(), MOVER.GetKing().GetCurrentColumn(), a_piece.GetColor())){
                            currentSpectrumMoves.add(candidate);
                        }
                    }
                }
            }else{
                continue;
            }
        }
        return currentSpectrumMoves;
    }
    
    /**/
    /*
    NAME
        public void AddCurrentRegularMoves(final Board a_board);
    
    SYNOPSIS
        public void AddCurrentRegularMoves(final Board a_board);
    
        Board a_board -----> The chessboard which contains the current game state.
    
    DESCRIPTION
        This method populates the current regular move array, taking into account which
        tiles the piece can actually visit on this turn. For example, no tile more than two
        spaces away can be visited on the first turn, and no tile more than one space
        away can be visited on each subsequent turn. No regular moves can be made if
        the space in front of this piece is blocked by any piece. This piece
        may not have any legal moves if the king is in check and the piece can't help him.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public static ArrayList<Move> AddCurrentRegularMoves(final Piece a_piece, final Board a_board, final Delta[] a_allRegularMoves){
        // Make copies of destination coordinates
        int newRow, newColumn;
        
        // limit will hold either 1 or 2, which will depend on what piece is in the way of the pawn
        final int LIMIT;
        
        // Instantiate an ArrayList to hold the move(s)
        ArrayList<Move> regularMoves = new ArrayList<>();
        
        // Determine if this pawn is blocked and return an empty list if it is
        if(a_piece.IsWhite()){
        	if(BoardUtilities.HasValidCoordinates(a_piece.GetCurrentRow() - Utilities.ONE, a_piece.GetCurrentColumn()) && a_board.GetTile(a_piece.GetCurrentRow() - Utilities.ONE, a_piece.GetCurrentColumn()).IsOccupied()){
        		return regularMoves;
        	}
        }else{
        	if(BoardUtilities.HasValidCoordinates(a_piece.GetCurrentRow() + Utilities.ONE, a_piece.GetCurrentColumn()) && a_board.GetTile(a_piece.GetCurrentRow() + Utilities.ONE, a_piece.GetCurrentColumn()).IsOccupied()){
        		return regularMoves;
        	}
        }
        
        // Since this pawn isn't blocked, determine how many tiles it can move
        if(!a_piece.HasMoved() && ((a_piece.IsWhite() && a_piece.GetCurrentRow() == Utilities.SIX) || (a_piece.IsBlack() && a_piece.GetCurrentRow() == Utilities.ONE))){
            LIMIT = Utilities.TWO;
        }else{
            LIMIT = Utilities.ONE;
        }        
        
        // Check that many tiles in front of the pawn to calculate move legality
        for(int index = Utilities.ZERO; index < LIMIT; index++){
            Board clone = null;
            Human tempWhite = new Human(ChessColor.WHITE, a_board);
            Human tempBlack = new Human(ChessColor.BLACK, a_board);
            
            try{
            	tempWhite.InitializePieces(a_board);
            	tempBlack.InitializePieces(a_board);
            }catch(Exception e){
            	e.printStackTrace();
            }
            
            final Player MOVER;
            
            if(a_piece.IsWhite()){
                MOVER = tempWhite;
            }else{
                MOVER = tempBlack;
            }
            
            // Reach the coordinates of the new move
            newRow = a_piece.GetCurrentRow() + a_allRegularMoves[index].GetRowDelta();
            newColumn = a_piece.GetCurrentColumn() + a_allRegularMoves[index].GetColumnDelta();
            
            // Do not allow this move if it is not to a valid tile
            if(BoardUtilities.HasValidCoordinates(newRow, newColumn)){
                RegularMove candidate = new RegularMove(a_piece, newRow, newColumn, a_board);
                                
                // Only proceed if this move does not capture another piece.
                if(a_board.GetTile(candidate.GetNewRow(), candidate.GetNewColumn()).IsEmpty()){
                    
                    clone = candidate.GetTransitionalBoard();
                    
                    try{
                    	tempWhite.InitializePieces(clone);
                    	tempBlack.InitializePieces(clone);
                    }catch(Exception e){
                    	e.printStackTrace();
                    }
                    
                    // Only add this move if it keeps the player's king safe
                    if(MoveEvaluation.IsKingSafe(clone, MOVER.GetKing().GetCurrentRow(), MOVER.GetKing().GetCurrentColumn(), MOVER.GetColor())){
                        regularMoves.add(candidate);
                    }
                }
            }else{
                continue;
            }
        }
        
        // Return the ArrayList
        return regularMoves;
    }
    
    /**/
    /*
    NAME
        public void AddCurrentAttackingMoves(final Piece a_piece, final Board a_board, final Delta[] a_allAttackingMoves);
    
    SYNOPSIS
        public void AddCurrentAttackingMoves(final Piece a_piece, final Board a_board, final Delta a_allAttackingMoves);
    
        Piece a_piece ----------------> The piece to be evaluated.
    
        Board a_board ----------------> The chessboard which contains the current game state.
        
        Delta[] a_allAttackingMoves --> The array of all possible moves.
    
    DESCRIPTION
        This method populates the current attacking move ArrayList, taking into account which
        tiles the piece can actually visit on this turn. For example, no tile diagonally
        ahead that does not contain an opposing piece can be visited. Only one attacking
        move may be added if this piece is either in column a or h. Also, this piece
        may not have any legal moves if its king is in check and the piece can't help him.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public static ArrayList<Move> AddCurrentAttackingMoves(final Piece a_piece, final Board a_board, final Delta[] a_allAttackingMoves){
        int newRow, newColumn;
        ArrayList<Move> attackingMoves = new ArrayList<>();
        
        // Check both diagonals
        for(int index = Utilities.ZERO; index < a_allAttackingMoves.length; index++){
            // Make deep copies of board and players
            Board clone = null;
            Human tempWhite = new Human(ChessColor.WHITE, a_board);
            Human tempBlack = new Human(ChessColor.BLACK, a_board);
            
            try{
            	tempWhite.InitializePieces(a_board);
            	tempBlack.InitializePieces(a_board);
            }catch(Exception e){
            	e.printStackTrace();
            }
            
            // Assign an alias to the moving player
            final Player MOVER;
            
            if(a_piece.IsWhite()){
                MOVER = tempWhite;
            }else{
                MOVER = tempBlack;
            }
            
            // Reach the current diagonal
            newRow = a_piece.GetCurrentRow() + a_allAttackingMoves[index].GetRowDelta();
            newColumn = a_piece.GetCurrentColumn() + a_allAttackingMoves[index].GetColumnDelta();
            
            // Do not add this move if the coordinates go off the board at either side
            if(BoardUtilities.HasValidCoordinates(newRow, newColumn)){

                final Piece VICTIM = a_board.GetTile(newRow, newColumn).GetPiece();
                if(VICTIM != null && a_piece.IsEnemy(VICTIM) && !VICTIM.IsKing()){
                    AttackingMove move = new AttackingMove(a_piece, newRow, newColumn, a_board);
                    
                    clone = move.GetTransitionalBoard();
                    
                    try{
                    	tempWhite.InitializePieces(clone);
                    	tempBlack.InitializePieces(clone);
                    }catch(Exception e){
                    	e.printStackTrace();
                    }
                    
                    // If this move keeps the player's king safe, add it.
                    if(MoveEvaluation.IsKingSafe(clone, MOVER.GetKing().GetCurrentRow(), MOVER.GetKing().GetCurrentColumn(), a_piece.GetColor())){
                        attackingMoves.add(move);
                    }
                }
            }else{
                continue;
            }
        }//End for loop
        // Return the ArrayList
        return attackingMoves;
    }
    
    /**/
    /*
    NAME
        public void AddCurrentEnPassantMoves(final Piece a_piece, final Board a_board);
    
    SYNOPSIS
        public void AddCurrentEnPassantMoves(final Piece a_piece, final Board a_board);
        
        Piece a_piece -----> The piece to be evaluated.
    
        Board a_board -----> The chessboard which contains the current game state.
    
    DESCRIPTION
        This method populates the current en passant move array. This will only get
        executed if the pawn is on the enemy pawn's rank where it would move out two
        squares on its initial move. It checks (up to) two spots to its side and determines
        if a freshly-moved enemy pawn has moved two spaces there. If so, this move is added
        for the current turn only. It will expire if the player does not use it on his/her
        next turn.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public static ArrayList<Move> AddCurrentEnPassantMoves(final Piece a_piece, final Board a_board){
        // enPassantMoves will hold the pawn's en passant moves
        ArrayList<Move> enPassantMoves = new ArrayList<>();
        
        // This type of move is only relevant to pawns
        if(!a_piece.IsPawn()){
        	return null;
        }
        
        // Eligibility criteria vary between colors
        if((a_piece.IsWhite() && a_piece.GetCurrentRow() == Utilities.THREE) || (a_piece.IsBlack() && a_piece.GetCurrentRow() == Utilities.FOUR)){        

            for(int index = Utilities.ZERO; index < MoveEvaluation.m_allEnPassantMoves.length; index++){
            
                // Copy the board and players
                Board clone = null;
                Human tempWhite = new Human(ChessColor.WHITE, a_board);
                Human tempBlack = new Human(ChessColor.BLACK, a_board);
                
                try{
                	tempWhite.InitializePieces(a_board);
                	tempBlack.InitializePieces(a_board);
                }catch(Exception e){
                	e.printStackTrace();
                }
                
                // Make an alias for the moving player
                final Player MOVER;
                
                if(a_piece.IsWhite()){
                    MOVER = tempWhite;
                }else{
                    MOVER = tempBlack;
                }
                
                // Declare variables to hold both victim and destination coordinates
                final int VICTIM_ROW = a_piece.GetCurrentRow() + MoveEvaluation.m_allEnPassantMoves[index].GetRowDelta();
                final int VICTIM_COLUMN = a_piece.GetCurrentColumn() + MoveEvaluation.m_allEnPassantMoves[index].GetColumnDelta();
                
                final int DESTINATION_ROW, DESTINATION_COLUMN = VICTIM_COLUMN;
                
                // Determine the destination row based on the color of the piece
                if(a_piece.IsWhite()){
                    DESTINATION_ROW = a_piece.GetCurrentRow() - Utilities.ONE;
                }else{
                    DESTINATION_ROW = a_piece.GetCurrentRow() + Utilities.ONE;
                }
                
                // Determine if the destination coordinates are valid before adding the move
                if(!BoardUtilities.HasValidCoordinates(VICTIM_ROW, VICTIM_COLUMN)){
                    continue;
                }else{
                    final Piece VICTIM = a_board.GetTile(VICTIM_ROW, VICTIM_COLUMN).GetPiece();
                    
                    try{
                        // Do not add the move unless the victim is an opposing pawn next to this one that moved 2 spaces on the previous move
                        if(VICTIM.Equals(DarkBlue.GetPreviouslyMoved())
                                && ((VICTIM.IsBlack() && VICTIM_ROW == DarkBlue.GetOriginalRow() + Utilities.TWO && DarkBlue.GetOriginalColumn() == VICTIM_COLUMN)
                                        || (VICTIM.IsWhite() && VICTIM_ROW == DarkBlue.GetOriginalRow() - Utilities.TWO && DarkBlue.GetOriginalColumn() == VICTIM_COLUMN))){
                        
                            EnPassantMove move = new EnPassantMove((Pawn)a_piece, DESTINATION_ROW, DESTINATION_COLUMN, (Pawn)VICTIM, a_board);
                            
                            // Make the move and reinitialize the pieces
                            clone = move.GetTransitionalBoard();
                            tempWhite.InitializePieces(clone);
                            tempBlack.InitializePieces(clone);
                            
                            // Do not allow this move unless the player's king is safe after executing it
                            if(MoveEvaluation.IsKingSafe(clone, MOVER.GetKing().GetCurrentRow(), MOVER.GetKing().GetCurrentColumn(), MOVER.GetColor())){
                                enPassantMoves.add(move);
                            }
                        }
                    }catch(Exception e){
                        continue;
                    }
                }
            }
        }

        return enPassantMoves;
    }
    
    /**/
    /*
    NAME
        public static boolean IsKingMovesSafe(final Board a_board, final King a_king);
    
    SYNOPSIS
        public static boolean IsKingMovesSafe(final Board a_board, final King a_king);
        
        Board a_board -------> The current board.
        
        King a_king ---------> The king to evaluate.

    DESCRIPTION
        This method returns if the three, five, or eight tiles going one square in every valid direction from the player's king
        are safe, i.e. there are no enemy pieces. If there's a friendly piece on
        one of these tiles, this tile is not evaluated further. If an empty tile is threatened
        by a nearby enemy piece, this entire method fails and returns false. If there's an enemy piece
        that can immediately threaten the king, this entire method fails and returns false.
        
    RETURNS
        True if every tile adjacent to the king is either safe or occupied by a friendly piece, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsKingMovesSafe(final Board a_board, final int a_row, final int a_column, final ChessColor a_color){
        int candidateRow = a_row, 
            candidateColumn = a_column;
        
        // Check all king moves
        for(int index = Utilities.ZERO; index < MoveEvaluation.m_allKingMoves.length; index++){
            candidateRow = a_row + MoveEvaluation.m_allKingMoves[index].GetRowDelta();
            candidateColumn = a_column + MoveEvaluation.m_allKingMoves[index].GetColumnDelta();
            if(!BoardUtilities.HasValidCoordinates(candidateRow, candidateColumn)){
                continue;
            }else{
                // Find the piece on the space
                final Piece NEIGHBOR = a_board.GetTile(candidateRow, candidateColumn).GetPiece();
                
                // Empty spaces are considered safe for now
                if(NEIGHBOR == null){
                	continue;
                }
                
                // Check diagonal directions
                if(a_color.IsBlack()){
                    // Unsafe if the neighbor is a white pawn (only if coming from below), queen, king, or bishop
                    if(candidateRow > a_row && candidateColumn < a_column && NEIGHBOR.IsWhite()
                            && (NEIGHBOR.IsPawn() || NEIGHBOR.IsKing() || NEIGHBOR.IsBishop() || NEIGHBOR.IsQueen())){// Lower left
                        return false;
                    }else if(candidateRow > a_row && candidateColumn > a_column && NEIGHBOR.IsWhite()
                            && (NEIGHBOR.IsPawn() || NEIGHBOR.IsKing() || NEIGHBOR.IsBishop() || NEIGHBOR.IsQueen())){// Lower right
                        return false;
                    }else if(candidateRow < a_row && candidateColumn < a_column && NEIGHBOR.IsWhite()
                            && (NEIGHBOR.IsKing() || NEIGHBOR.IsBishop() || NEIGHBOR.IsQueen())){// Upper left
                        return false;
                    }else if(candidateRow < a_row && candidateColumn > a_column && NEIGHBOR.IsWhite()
                            && (NEIGHBOR.IsKing() || NEIGHBOR.IsBishop() || NEIGHBOR.IsQueen())){// Upper right
                        return false;
                    }
                }else if(a_color.IsWhite()){
                    // Unsafe if the neighbor is a black pawn (only if coming from above), queen, king, or bishop             	
                	if(candidateRow > a_row && candidateColumn < a_column && NEIGHBOR.IsBlack()
                            && (NEIGHBOR.IsKing() || NEIGHBOR.IsBishop() || NEIGHBOR.IsQueen())){// Lower left
                        return false;
                    }else if(candidateRow > a_row && candidateColumn > a_column && NEIGHBOR.IsBlack()
                            && (NEIGHBOR.IsKing() || NEIGHBOR.IsBishop() || NEIGHBOR.IsQueen())){// Lower right
                        return false;
                    }else if(candidateRow < a_row && candidateColumn < a_column && NEIGHBOR.IsBlack()
                            && (NEIGHBOR.IsPawn() || NEIGHBOR.IsKing() || NEIGHBOR.IsBishop() || NEIGHBOR.IsQueen())){// Upper left
                        return false;
                    }else if(candidateRow < a_row && candidateColumn > a_column && NEIGHBOR.IsBlack()
                            && (NEIGHBOR.IsPawn() || NEIGHBOR.IsKing() || NEIGHBOR.IsBishop() || NEIGHBOR.IsQueen())){// Upper right
                        return false;
                    }
                }
                
                // Check horizontal and vertical directions
                if(candidateRow < a_row && candidateColumn == a_column && NEIGHBOR != null && NEIGHBOR.GetColor().IsEnemy(a_color)
                        && (NEIGHBOR.IsRook() || NEIGHBOR.IsQueen() || NEIGHBOR.IsKing())){// Up
                    return false;
                }else if(candidateRow > a_row && candidateColumn == a_column && NEIGHBOR != null && NEIGHBOR.GetColor().IsEnemy(a_color)
                        && (NEIGHBOR.IsRook() || NEIGHBOR.IsQueen() || NEIGHBOR.IsKing())){// Down
                    return false;
                }else if(candidateRow == a_row && candidateColumn < a_column && NEIGHBOR != null && NEIGHBOR.GetColor().IsEnemy(a_color)
                        && (NEIGHBOR.IsRook() || NEIGHBOR.IsQueen() || NEIGHBOR.IsKing())){// Left
                    return false;
                }else if(candidateRow == a_row && candidateColumn > a_column && NEIGHBOR != null && NEIGHBOR.GetColor().IsEnemy(a_color)
                        && (NEIGHBOR.IsRook() || NEIGHBOR.IsQueen() || NEIGHBOR.IsKing())){// Right
                    return false;
                }else{
                    continue;
                }
            }
        }
        return true;
    }
    
    /**/
    /*
    NAME
        public static boolean IsKnightMovesSafe(final Board a_board, final King a_king);
    
    SYNOPSIS
        public static boolean IsKnightMovesSafe(final Board a_board, final King a_king);
        
        Board a_board -------> The current board.
        
        King a_king ---------> The king to evaluate.

    DESCRIPTION
        This method checks the three, five, or eight tiles where knights could be from the king. 
        If there's a friendly piece on one of these tiles, or if this tile is invalid or empty, it is not evaluated further. 
        If there's an enemy knight that can immediately threaten the king, this entire method fails and returns false.
        
    RETURNS
        True if every empty tile adjacent to the king is either safe or occupied by a friendly piece, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsKnightMovesSafe(final Board a_board, final int a_row, final int a_column, final ChessColor a_color){
        int candidateRow, candidateColumn;
        
        // Knight's moves
        candidateRow = a_row;
        candidateColumn = a_column;

        // Check all knight's moves
        try{
            for(int index = Utilities.ZERO; index < MoveEvaluation.m_allKnightMoves.length; index++){        
                
                candidateRow = a_row + MoveEvaluation.m_allKnightMoves[index].GetRowDelta();
                candidateColumn = a_column + MoveEvaluation.m_allKnightMoves[index].GetColumnDelta();    
                
                // This king is not safe if a knight is threatening him from an L-shape away
                if(BoardUtilities.HasValidCoordinates(candidateRow, candidateColumn)){
                        if(a_board.GetTile(candidateRow, candidateColumn).IsOccupied()
                                && a_board.GetTile(candidateRow, candidateColumn).GetPiece().IsKnight() 
                                && a_board.GetTile(candidateRow, candidateColumn).GetPiece().GetColor().IsEnemy(a_color)){
                        return false;
                    }
                }else{
                    continue;
                }
            }        
        }catch(Exception e){}
        
        return true;
    }
    
    /**/
    /*
    NAME
        public boolean IsDirectionSafe(final Board a_board, final int a_row, final int a_column, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta);
    
    SYNOPSIS
        public boolean IsDirectionSafe(final Board a_board, final int a_row, final int a_column, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta);
        
        Board a_board ----------> The current board.
        
        int a_row --------------> The king's row.
        
        int a_column -----------> The king's column.
        
        ChessColor a_color -----> The king's color.
        
        int a_rowDelta ---------> The difference in rows between two sequential tiles.
        
        int a_columnDelta ------> The difference in columns between two sequential tiles.
    
    DESCRIPTION
        This method returns if the tiles going this direction from the player's king
        are safe, i.e. there are no enemy pieces or the row is entirely empty. 
        If there's a friendly piece blocking an enemy piece that would put the king into check, this direction is considered safe.
        If an empty tile is encountered, the program continues with the next one out until a piece is encountered or
        the coordinates are no longer valid. If this happens, this direction is deemed to be completely safe.
        
    RETURNS
        boolean: True if this diagonal is safe, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Adapted from kingSafe() method in "Alpha Beta Chess" by Jonathan Warkentin, 
        https://sites.google.com/site/jonathanwarkentinlogiccrazy/Downhome/chess
    */
    public static boolean IsDirectionSafe(final Board a_board, final int a_row, final int a_column, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta){
        
        int candidateRow, candidateColumn;

        // Advance one tile in the current direction
        candidateRow = a_row + a_rowDelta;
        candidateColumn = a_column + a_columnDelta;
        
        // Check in this direction
        try{
            while(BoardUtilities.HasValidCoordinates(candidateRow, candidateColumn)){
                if(a_board.GetTile(candidateRow, candidateColumn).IsOccupied() 
                && a_board.GetTile(candidateRow, candidateColumn).GetPiece().GetColor().IsAlly(a_color)){
                    // The direction is safe if a friendly piece is found
                    break;
                }else if(a_board.GetTile(candidateRow, candidateColumn).IsOccupied()
                        && a_board.GetTile(candidateRow, candidateColumn).GetPiece().GetColor().IsEnemy(a_color)){    
                    // Find out which kind of enemy piece is on the tile and determine if it's threatening the king
                    return EvaluateDirection(a_board, candidateRow, candidateColumn, a_color, a_rowDelta, a_columnDelta);
                }
                // Go to the next tile in the sequence
                candidateRow += a_rowDelta;
                candidateColumn += a_columnDelta;
            }
        }catch(Exception e){}// Stop if the tile goes out of bounds
        
        // Reaching here means that all tiles have been checked and there are no threats in this direction
        return true;
    }
    
    /**/
    /*
    NAME
        public static boolean IsKingSafe(final Board a_board, final King a_king);
    
    SYNOPSIS
        public static boolean IsKingSafe(final Board a_board, final King a_king);
        
        Board a_board -------> The current board.
        
        King a_king ---------> The king to evaluate.

    DESCRIPTION
        This method checks all the tiles on the board in every direction from the king, including knight moves.
        If there's a friendly piece on a tile in a linear direction, this direction is deemed to be safe 
        and no other tiles in that direction are evaluated.
        If a friendly piece is on a tile in the knight or king move spectrum, the program continues with the next valid tile, if any.
        If the current tile is empty, the method continues to the next tile in the current direction or set.
        If the current tile is out of bounds in a linear direction, the method stops executing and the direction is deemed to be safe.
        If the current tile is out of bounds in a spectrum (king or knight moves), the method continues executing with the next tile, if any.
        If an enemy piece that can attack in the current direction or set is found, this direction is deemed to be unsafe
        and the method immediately stops and short-circuits to false. 
        If all tiles are exhaustively proven to be safe, this method returns true.

    RETURNS
        True if every empty tile with access to the king is either safe or occupied by a friendly piece, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsKingSafe(final Board a_board, final int a_row, final int a_column, final ChessColor a_color){
        return MoveEvaluation.IsKingMovesSafe(a_board, a_row, a_column, a_color) 
                && MoveEvaluation.IsKnightMovesSafe(a_board, a_row, a_column, a_color) 
                && MoveEvaluation.IsDirectionSafe(a_board, a_row, a_column, a_color, Utilities.NEGATIVE_ONE, Utilities.NEGATIVE_ONE) // Upper left
                && MoveEvaluation.IsDirectionSafe(a_board, a_row, a_column, a_color, Utilities.NEGATIVE_ONE, Utilities.ONE) // Upper right
                && MoveEvaluation.IsDirectionSafe(a_board, a_row, a_column, a_color, Utilities.ONE, Utilities.NEGATIVE_ONE) // Lower left
                && MoveEvaluation.IsDirectionSafe(a_board, a_row, a_column, a_color, Utilities.ONE, Utilities.ONE) // Lower right
                && MoveEvaluation.IsDirectionSafe(a_board, a_row, a_column, a_color, Utilities.NEGATIVE_ONE, Utilities.ZERO) // Up
                && MoveEvaluation.IsDirectionSafe(a_board, a_row, a_column, a_color, Utilities.ONE, Utilities.ZERO) //Down
                && MoveEvaluation.IsDirectionSafe(a_board, a_row, a_column, a_color, Utilities.ZERO, Utilities.NEGATIVE_ONE) // Left
                && MoveEvaluation.IsDirectionSafe(a_board, a_row, a_column, a_color, Utilities.ZERO, Utilities.ONE); // Right
    }
    
    /**/
    /*
    NAME
        public static boolean EvaluateDirection(final Board a_board, final int a_candidateRow, final int a_candidateColumn, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta);
    
    SYNOPSIS
        public static boolean EvaluateDirection(final Board a_board, final int a_candidateRow, final int a_candidateColumn, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta);
    
        Board a_board -----------> The board to be evaluated.
        
        int a_candidateRow ------> The row to be evaluated.
        
        int a_candidateColumn ---> The column to be evaluated.
        
        ChessColor a_color ------> The color of the king.
        
        int a_rowDelta ----------> The difference between rows.
        
        int a_columnDelta -------> The difference between columns.
    
    DESCRIPTION
        This method evaluates if there's a piece in a certain direction
        that could be threatening the king.
        
        For example, in a diagonal direction, a bishop or a queen is considered a threat.
        Likewise, in a horizontal direction, a rook or a queen is considered a threat.
        
        The deltas are evaluated by separate methods.
        
        If one of those pieces is found under those conditions, this direction is not considered to be safe.
    
    RETURNS
        True if the direction is safe, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean EvaluateDirection(final Board a_board, final int a_candidateRow, final int a_candidateColumn, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta){
        // Determine if this direction is diagonal or horizontal (It will always be one or the other)
        if(IsDiagonal(a_rowDelta, a_columnDelta)){
            return EvaluateDiagonal(a_board, a_candidateRow, a_candidateColumn, a_color, a_rowDelta, a_columnDelta);
        }else{
            return EvaluateHorizontal(a_board, a_candidateRow, a_candidateColumn, a_color, a_rowDelta, a_columnDelta);
        }
    }
    
    /**/
    /*
    NAME
        public static boolean EvaluateDiagonal(final Board a_board, final int a_candidateRow, final int a_candidateColumn, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta);
    
    SYNOPSIS
        public static boolean EvaluateDiagonal(final Board a_board, final int a_candidateRow, final int a_candidateColumn, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta);
    
        Board a_board -----------> The board to be evaluated.
        
        int a_candidateRow ------> The row to be evaluated.
        
        int a_candidateColumn ---> The column to be evaluated.
        
        ChessColor a_color ------> The color of the king.
        
        int a_rowDelta ----------> The difference between rows.
        
        int a_columnDelta -------> The difference between columns.
    
    DESCRIPTION
        This method evaluates if there's an enemy bishop or queen on
        the current tile.
                
        If one of those pieces is found, this direction is not considered to be safe.
    
    RETURNS
        True if the direction is safe, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean EvaluateDiagonal(final Board a_board, final int a_candidateRow, final int a_candidateColumn, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta){
        // If there's an enemy bishop or queen in this direction, it is not safe
        if((a_board.GetTile(a_candidateRow, a_candidateColumn).GetPiece().IsBishop() || a_board.GetTile(a_candidateRow,  a_candidateColumn).GetPiece().IsQueen())
                && a_board.GetTile(a_candidateRow, a_candidateColumn).GetPiece().GetColor().IsEnemy(a_color)){
            return false;
        }else{
            return true;
        }
    }
    
    /**/
    /*
    NAME
        public static boolean EvaluateHorizontal(final Board a_board, final int a_candidateRow, final int a_candidateColumn, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta);
    
    SYNOPSIS
        public static boolean EvaluateHorizontal(final Board a_board, final int a_candidateRow, final int a_candidateColumn, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta);
    
        Board a_board -----------> The board to be evaluated.
        
        int a_candidateRow ------> The row to be evaluated.
        
        int a_candidateColumn ---> The column to be evaluated.
        
        ChessColor a_color ------> The color of the king.
        
        int a_rowDelta ----------> The difference between rows.
        
        int a_columnDelta -------> The difference between columns.
    
    DESCRIPTION
        This method evaluates if there's an enemy rook or queen on
        the current tile.
                
        If one of those pieces is found, this direction is not considered to be safe.
    
    RETURNS
        True if the direction is safe, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean EvaluateHorizontal(final Board a_board, final int a_candidateRow, final int a_candidateColumn, final ChessColor a_color, final int a_rowDelta, final int a_columnDelta){
        // If there's an enemy rook or queen in this direction, it is not safe
        if((a_board.GetTile(a_candidateRow, a_candidateColumn).GetPiece().IsRook() || a_board.GetTile(a_candidateRow, a_candidateColumn).GetPiece().IsQueen())
                && a_board.GetTile(a_candidateRow, a_candidateColumn).GetPiece().GetColor().IsEnemy(a_color)){
            return false;
        }else{
            return true;
        }
    }
    
    /**/
    /*
    NAME
        public static boolean IsDiagonal(final int a_rowDelta, final int a_columnDelta);
    
    SYNOPSIS
        public static boolean IsDiagonal(final int a_rowDelta, final int a_columnDelta);

        int a_rowDelta ----------> The difference between rows.
        
        int a_columnDelta -------> The difference between columns.
    
    DESCRIPTION
        This method evaluates if this set of deltas is diagonal.
    
    RETURNS
        True if the deltas represent a diagonal direction, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsDiagonal(final int a_rowDelta, final int a_columnDelta){
        // Return true if the deltas are any of the following
        return (a_rowDelta == Utilities.ONE && a_columnDelta == Utilities.NEGATIVE_ONE)// Down and left
                || (a_rowDelta == Utilities.ONE && a_columnDelta == Utilities.ONE)// Down and right
                || (a_rowDelta == Utilities.NEGATIVE_ONE && a_columnDelta == Utilities.ONE)// Up and right
                || (a_rowDelta == Utilities.NEGATIVE_ONE && a_columnDelta == Utilities.NEGATIVE_ONE);// Up and left
    }
    
    /**/
    /*
    NAME
        public static boolean IsHorizontal(final int a_rowDelta, final int a_columnDelta);
    
    SYNOPSIS
        public static boolean IsHorizontal(final int a_rowDelta, final int a_columnDelta);

        int a_rowDelta ----------> The difference between rows.
        
        int a_columnDelta -------> The difference between columns.
    
    DESCRIPTION
        This method evaluates if this set of deltas is horizontal.
    
    RETURNS
        True if the deltas represent a diagonal direction, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsHorizontal(final int a_rowDelta, final int a_columnDelta){
        // Return true if the deltas are any of the following
        return (a_rowDelta == Utilities.ONE && a_columnDelta == Utilities.ZERO)// Down
                || (a_rowDelta == Utilities.ZERO && a_columnDelta == Utilities.ONE)// Right
                || (a_rowDelta == Utilities.NEGATIVE_ONE && a_columnDelta == Utilities.ZERO)// Up
                || (a_rowDelta == Utilities.ZERO && a_columnDelta == Utilities.NEGATIVE_ONE);// Left
    }
    
    /**/
    /*
    NAME
        public static ArrayList<Move> CopyCurrentMoves(final ArrayList<Move> a_movesToCopy);
    
    SYNOPSIS
        public static ArrayList<Move> CopyCurrentMoves(final ArrayList<Move> a_movesToCopy);
    
        ArrayList<Move> a_movesToCopy -------------> The ArrayList of moves to be copied.

    DESCRIPTION
        This method makes copies of moves from the a_movesToCopy ArrayList and instantiates
        the copies by type.
    
    RETURNS
        ArrayList<Move> copiedMoves: The moves copied from a_movesToCopy.
    
    AUTHOR
        Ryan King
    */
    public static ArrayList<Move> CopyCurrentMoves(final ArrayList<Move> a_movesToCopy){
        final ArrayList<Move> COPIED_MOVES = new ArrayList<>();
        
        for(int index = Utilities.ZERO; index < a_movesToCopy.size(); index++){
            final Move NEXT = a_movesToCopy.get(index);
            
            if(NEXT.IsEnPassant()){
                COPIED_MOVES.add(new EnPassantMove((Pawn)NEXT.GetPiece(), NEXT.GetNewRow(), NEXT.GetNewColumn(), (Pawn)NEXT.GetVictim(), NEXT.GetInitialBoard()));
            }else if(NEXT.IsCastling()){
                final CastlingMove CASTLE = (CastlingMove) NEXT;
                COPIED_MOVES.add(new CastlingMove(((King)CASTLE.GetPiece()), CASTLE.GetNewRow(), CASTLE.GetNewColumn(), NEXT.GetInitialBoard()));
            }else if(NEXT.IsAttacking()){
                COPIED_MOVES.add(new AttackingMove(NEXT.GetPiece(), NEXT.GetNewRow(), NEXT.GetNewColumn(), NEXT.GetInitialBoard()));
            }else{
                COPIED_MOVES.add(new RegularMove(NEXT.GetPiece(), NEXT.GetNewRow(), NEXT.GetNewColumn(), NEXT.GetInitialBoard()));
            }
        }
        
        return COPIED_MOVES;
    }
    
    /*
    NAME
        public static boolean IsEnPassantMove();
    
    SYNOPSIS
        public static boolean IsEnPassantMove();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the move to be generated is an en passant move,
        which is a special capture involving pawns. If the moving piece is a pawn
        on its fifth rank, and a piece to its left or right is an enemy pawn that
        just advanced 2 tiles on the previous move and would have been captured by
        this pawn had it only moved 1 tile, and the destination tilefor the first 
        pawn is an empty tile diagonally in front of it, then this move is deemed 
        to be an en passant move.
    
    RETURNS
        True if the move is en passant, and false otherwise.
        One of these two options will always occur.
        
    AUTHOR
        Ryan King
    */
    public static boolean IsEnPassantMove(final Piece a_candidate, final int a_destinationRow, final int a_destinationColumn, final Board a_board){       
        return a_candidate.IsPawn()
        		&& ((a_candidate.IsWhite() && a_candidate.GetCurrentRow() == Utilities.THREE) || (a_candidate.IsBlack() && a_candidate.GetCurrentRow() == Utilities.FOUR))
                    && a_board.GetTile(a_destinationRow, a_destinationColumn).IsEmpty()
                    && (((a_candidate.IsWhite() && a_destinationRow == a_candidate.GetCurrentRow() - Utilities.ONE) && (a_destinationColumn == a_candidate.GetCurrentColumn() - Utilities.ONE || a_destinationColumn == a_candidate.GetCurrentColumn() + Utilities.ONE) && BoardUtilities.HasValidCoordinates(a_destinationRow + Utilities.ONE, a_destinationColumn) && a_board.GetTile(a_destinationRow + Utilities.ONE, a_destinationColumn).GetPiece().IsPawn() && a_board.GetTile(a_destinationRow + Utilities.ONE, a_destinationColumn).GetPiece().IsEnemy(a_candidate))
                    || ((a_candidate.IsBlack() && a_destinationRow == a_candidate.GetCurrentRow() + Utilities.ONE) && (a_destinationColumn == a_candidate.GetCurrentColumn() - Utilities.ONE || a_destinationColumn == a_candidate.GetCurrentColumn() + Utilities.ONE)) && BoardUtilities.HasValidCoordinates(a_destinationRow - Utilities.ONE, a_destinationColumn) && a_board.GetTile(a_destinationRow - Utilities.ONE, a_destinationColumn).GetPiece().IsPawn() && a_board.GetTile(a_destinationRow - Utilities.ONE, a_destinationColumn).GetPiece().IsEnemy(a_candidate));
    }

    /*
    NAME
        private final boolean IsCastlingMove();
    
    SYNOPSIS
        private final boolean IsCastlingMove();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the given move
        is a castling move or not.
        
    RETURNS
        True if the move is a castling move, 
        or false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsCastlingMove(final Piece a_candidate, final int a_sourceRow, final int a_sourceColumn, final int a_destinationRow, final int a_destinationColumn){
        return a_candidate.IsKing()
                && a_destinationRow == a_sourceRow 
                && (a_destinationColumn == a_sourceColumn + Utilities.TWO 
                || a_destinationColumn == a_sourceColumn - Utilities.TWO);
    }   
}