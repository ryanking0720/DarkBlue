package com.DarkBlue.Player;

import com.DarkBlue.Board.Board;
import com.DarkBlue.GUI.DarkBlue;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Factory;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Move.Move;
import com.DarkBlue.Move.RegularMove;
import com.DarkBlue.Piece.Pawn;
import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Move.AttackingMove;
import com.DarkBlue.Move.CastlingMove;
import com.DarkBlue.Move.EnPassantMove;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * This class is the bread and butter of the computer player.
 * 
 * It has a move searching algorithm that finds the best moves
 * based on its value in terms of piece quantity and position.
 * 
 * These values are determined by evaluating the value of each piece
 * as well as the value of the tile it is resting on. Arrays in the 
 * MoveEvaluation interface are brought in to be searched. Each piece
 * has its own array even if it's a different color, except for the
 * queen and the knight. Each board evaluation is a sum of these numbers.
 * 
 * The values are analyzed to determine which is either highest or lowest,
 * depending on the player's color. Alpha-beta pruning is used to establish
 * lower and upper bounds that can be checked in order to get rid of irrelevant
 * moves that will not change the state of the board much from what has already been discovered.
 * 
 * All moves are sorted when they are taken from each player. Sorted order is as follows:
 * 
 * Moves that put the opponent into checkmate
 * Moves that put the opponent into check
 * Attacking moves
 * Regular moves
 * En passant moves
 */
public class Minimax{

	// Symbolic constants
	public static final double PAWN_VALUE = 10;
	public static final double BISHOP_OR_KNIGHT_VALUE = 30;
	public static final double ROOK_VALUE = 50;
	public static final double QUEEN_VALUE = 90;
	public static final double KING_VALUE = 900;

	/**/
    /*
    NAME
        private Minimax();
    
    SYNOPSIS
        private Minimax();
        
        No parameters.
       
    DESCRIPTION
        This constructor is private and not callable.
        Trying to call it will throw a RuntimeException
        because all Minimax fields are static and the class
       	therefore does not need an instance to run.

    RETURNS
        Nothing
    
    AUTHOR
        Amir Afghani, Black Widow Chess: 
    */
	private Minimax(){
		throw new RuntimeException("You cannot instantiate me!");
	}
	
	
	/**/
    /*
    NAME
        public static final Move MinimaxRoot(final int a_depth, final Board a_board, final Player a_white, final Player a_black, final boolean a_isMaximizer);
    
    SYNOPSIS
        public static final Move MinimaxRoot(final int a_depth, final Board a_board, final Player a_white, final Player a_black, final boolean a_isMaximizer);
    
    	int a_depth --------------> The AI search depth.
    
        Board a_board ------------> The board to evaluate.
        
        Player a_white -----------> The white player.
        
        Player a_black -----------> The black player.
        
        boolean a_isMaximizer ----> If the computer player is the maximizing player.
       
    DESCRIPTION
        This method searches for the best possible move out of the pool of possible moves for the computer player.
        It maximizes and minimizes the board recursively depending on who's moving.
        If a move is found that places the opponent's king in checkmate, it will break out immediately and return that move.

    RETURNS
        Move bestMove: The computer's best possible move.
    
    AUTHOR
        Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://github.com/lhartikk/simple-chess-ai/blob/master/script.js
        Modifications written specifically for this engine by Ryan King.
    */
	public static final Move MinimaxRoot(final int a_depth, final Board a_board, final Player a_white, final Player a_black, final boolean a_isMaximizer){
		// bestMove will hold the best move found by the board evaluation
		Move bestMove = null;
		
		// mover will be an alias of the player whose turn it is
		Player mover = (a_board.WhoseTurnIsIt().IsWhite() ? a_white : a_black);
		
		// Order the moves so the best ones come first (e.g. checkmate, check, attacks, castling, etc.)
		final ArrayList<Move> moves = Minimax.Sort((a_board.WhoseTurnIsIt().IsWhite() ? a_white : a_black), (a_board.WhoseTurnIsIt().IsWhite() ? a_black : a_white), a_board, a_depth);
		
		// Set default values for our variables
		double currentValue = Utilities.ZERO;
		double bestValue = Integer.MIN_VALUE;
		
		for(Move move : moves){
			// result will hold the board after the move has been made
			final Board result = move.GetTransitionalBoard();
			
			// tempWhite and tempBlack will be new players which will be evaluated independently of the ones passed in
			final Player tempWhite = new Human(ChessColor.WHITE, result);
			final Player tempBlack = new Human(ChessColor.BLACK, result);
			
			tempWhite.Refresh(result);
			tempBlack.Refresh(result);
			
			// Recursively search for the best value
			currentValue = Recurse(a_depth - Utilities.ONE, result, tempWhite, tempBlack, Integer.MIN_VALUE, Integer.MAX_VALUE, !a_isMaximizer);
			
			// Update the value if the next one found is better; update the move accordingly
			if(currentValue >= bestValue){
				bestValue = currentValue;
				bestMove = move;
			}
		}
				
		return bestMove;		
	}
	
	/**/
    /*
    NAME
        public static final double Recurse(final int a_depth, final Board a_board, final Player a_white, final Player a_black, double a_alpha, double a_beta, final boolean a_isMaximizer);
    
    SYNOPSIS
        public static final double Recurse(final int a_depth, final Board a_board, final Player a_white, final Player a_black, double a_alpha, double a_beta, final boolean a_isMaximizer);
    
    	int a_depth --------------> The AI search depth.
    
        Board a_board ------------> The board to evaluate.
        
        Player a_white -----------> The white player.
        
        Player a_black -----------> The black player.
        
        double a_alpha -----------> The best value for the maximizer.
        
        double a_beta ------------> The best value for the minimizer.
        
        boolean a_isMaximizer ----> If the player is the maximizing player.
       
    DESCRIPTION
        This method searches for the best possible board value from the pool of possible moves for the computer player.
        It maximizes and minimizes the board recursively depending on who's moving.
        This uses alpha-beta pruning, so moves that are determined to give a value lower than alpha or higher than beta will be ignored.
        If a move is found that places the opponent's king in checkmate, it will break out immediately and return that move.

    RETURNS
        double bestValue: The best board evaluation found.
    
    AUTHOR
    	Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://github.com/lhartikk/simple-chess-ai/blob/master/script.js
        Modifications written specifically for this engine by Ryan King.
    */
	public static final double Recurse(final int a_depth, final Board a_board, final Player a_white, final Player a_black, double a_alpha, double a_beta, final boolean a_isMaximizer){
		// Base case: The search depth is as deep as it can go
		if(a_depth == Utilities.ZERO){
			return (DarkBlue.GetComputerColor().IsBlack() ? -Evaluate(a_board) : Evaluate(a_board));
		}

		// moves will hold the current player's moves
		final ArrayList<Move> moves = Minimax.Sort((a_board.WhoseTurnIsIt().IsWhite() ? a_white : a_black), (a_board.WhoseTurnIsIt().IsWhite() ? a_black : a_white), a_board, a_depth);
		
		// bestValue will hold the current best board evaluation
		double bestValue;
		
		if(a_isMaximizer){
			// All values found will be higher than this
			bestValue = Integer.MIN_VALUE;
			
			for(Move move : moves){
				// Make a deep copy of the board with the move made on it
				final Board result = move.GetTransitionalBoard();
				
				// Initialize temporary players to determine evaluations on this board
				final Player tempWhite = new Human(ChessColor.WHITE, result);
				final Player tempBlack = new Human(ChessColor.BLACK, result);
				
				tempWhite.Refresh(result);
				tempBlack.Refresh(result);
				
				// currentPlayer will be an alias to whoever is playing next
				final Player currentPlayer = (result.WhoseTurnIsIt().IsWhite() ? tempWhite : tempBlack);
				
				// promotedPawn will contain a pawn that can be promoted if the player has one
				final Pawn promotedPawn = currentPlayer.GetPromotedPawn(result);
				
				// Evaluate the move as normal if no promotion can be made
				if(promotedPawn == null){
					// Find the highest value recursively
					bestValue = Math.max(bestValue, Recurse(a_depth - Utilities.ONE, result, tempWhite, tempBlack, a_alpha, a_beta, !a_isMaximizer));
				
					// Keep track of the boundaries
					a_alpha = Math.max(a_alpha, bestValue);
				}else{
					// Change the pawn to a promoted piece and then continue evaluating
					for(int i = Utilities.ZERO; i < Utilities.FOUR; i++){
						final Board promotion = promotedPawn.Promote(result, i);
						
						tempWhite.Refresh(promotion);
						tempBlack.Refresh(promotion);
						
						// Find the highest value recursively
						bestValue = Math.max(bestValue, Recurse(a_depth - Utilities.ONE, promotion, tempWhite, tempBlack, a_alpha, a_beta, !a_isMaximizer));
					
						// Keep track of the boundaries
						a_alpha = Math.max(a_alpha, bestValue);
						
						if(a_beta <= a_alpha){
							return bestValue;
						}
					}
				}
				// Discontinue evaluating if the lower bound is worse
				if(a_beta <= a_alpha){
					return bestValue;
				}
			}
			
			return bestValue;
		}else{
			// All values found will be lower than this
			bestValue = Integer.MAX_VALUE;
			
			for(Move move : moves){
				// Make a deep copy of the board with the move made on it
				final Board result = move.GetTransitionalBoard();
				
				// Initialize temporary players to determine evaluations on this board
				final Player tempWhite = new Human(ChessColor.WHITE, result);
				final Player tempBlack = new Human(ChessColor.BLACK, result);
				
				tempWhite.Refresh(result);
				tempBlack.Refresh(result);
				
				// currentPlayer will be an alias to whoever is playing next
				final Player currentPlayer = (result.WhoseTurnIsIt().IsWhite() ? tempWhite : tempBlack);
				
				// promotedPawn will contain a pawn that can be promoted if the player has one
				final Pawn promotedPawn = currentPlayer.GetPromotedPawn(result);
				
				// Evaluate the move as normal if no promotion can be made
				if(promotedPawn == null){
					// Find the lowest value recursively
					bestValue = Math.min(bestValue, Recurse(a_depth - Utilities.ONE, result, tempWhite, tempBlack, a_alpha, a_beta, !a_isMaximizer));
				
					// Keep track of the boundaries
					a_beta = Math.min(a_beta, bestValue);
				}else{
					// Change the pawn to a promoted piece and then continue evaluating
					for(int i = Utilities.ZERO; i < Utilities.FOUR; i++){
						final Board promotion = promotedPawn.Promote(result, i);
						
						tempWhite.Refresh(promotion);
						tempBlack.Refresh(promotion);
						
						// Find the highest value recursively
						bestValue = Math.min(bestValue, Recurse(a_depth - Utilities.ONE, promotion, tempWhite, tempBlack, a_alpha, a_beta, !a_isMaximizer));
					
						// Keep track of the boundaries
						a_beta = Math.min(a_beta, bestValue);
						
						if(a_beta <= a_alpha){
							return bestValue;
						}
					}
				}
				
				// Discontinue evaluating if the lower bound is worse
				if(a_beta <= a_alpha){
					return bestValue;
				}
			}
			
			return bestValue;
		}
	}
	
	/**/
    /*
    NAME
        public static final double Evaluate(final Board a_board);
    
    SYNOPSIS
        public static final double Evaluate(final Board a_board);
    
        Board a_board ------------> The board to evaluate.
       
    DESCRIPTION
        This method evaluates all pieces and their positions on the board.

    RETURNS
        double evaluation: The value of all pieces on the board.
    
    AUTHOR
    	Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://github.com/lhartikk/simple-chess-ai/blob/master/script.js
        Modifications written specifically for this engine by Ryan King.
    */
	public static final double Evaluate(final Board a_board){
		double evaluation = Utilities.ZERO;
		
		// Evaluate every tile of the board
		for(int i = Utilities.ZERO; i < Utilities.SIXTY_FOUR; i++){
			int row = i / Utilities.EIGHT;
			int column = i % Utilities.EIGHT;
			
			// Do not evaluate any empty tiles
			if(a_board.GetTile(row, column).IsEmpty()){
				continue;
			}
			
			// Get the value of the piece
			final Piece piece = a_board.GetTile(row, column).GetPiece();
			
			evaluation += GetPieceValue(piece, row, column);
		}
		
		return evaluation;
	}
	
	/**/
    /*
    NAME
        public static final double GetPieceValue(final Piece a_piece, final int a_x, final int a_y);
    
    SYNOPSIS
        public static final double GetPieceValue(final Piece a_piece, final int a_x, final int a_y);
    
        Piece a_piece --------------> The piece to evaluate.
        
        int a_x --------------------> The row the piece is on.
        
        int a_y --------------------> The column the piece is on.
       
    DESCRIPTION
        This method evaluates a piece on its tile.

    RETURNS
        double: The value of the given piece on its tile, or 0 if the piece was invalid.
        One of these two options will always occur.
    
    AUTHOR
    	Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://github.com/lhartikk/simple-chess-ai/blob/master/script.js
        Modifications written specifically for this engine by Ryan King.
    */
	public static final double GetPieceValue(final Piece a_piece, final int a_x, final int a_y){
		// Null arguments do not return any value of significance
	    if (a_piece == null){
	        return Utilities.ZERO;
	    }

	    // Find the absolute value of the piece, e.g. 10, 30, 50, 70, 90, or 900
	    final double absoluteValue = GetAbsoluteValue(a_piece, a_x ,a_y);
	    
	    // Negate the value if the piece is black or keep it positive if it's white
	    return (a_piece.IsWhite() ? absoluteValue : -absoluteValue);
	}

	/**/
    /*
    NAME
        public static final double GetAbsoluteValue(final Piece a_piece, final int a_x, final int a_y);
    
    SYNOPSIS
        public static final double GetAbsoluteValue(final Piece a_piece, final int a_x, final int a_y);
    
        Piece a_piece --------------> The piece to evaluate.
        
        int a_x --------------------> The row the piece is on.
        
        int a_y --------------------> The column the piece is on.
       
    DESCRIPTION
        This method evaluates a piece on its tile by returning its absolute value.
        Negation, if needed, will be performed later.

    RETURNS
        double: The value of the given piece on its tile, or 0 if the piece was invalid.
        One of these two options will always occur.
    
    AUTHOR
    	Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://github.com/lhartikk/simple-chess-ai/blob/master/script.js
        Modifications written specifically for this engine by Ryan King.
    */
	public static final double GetAbsoluteValue(final Piece a_piece, final int a_x , final int a_y){
		// Idiot proofing in case of null arguments
		if(a_piece == null || (!BoardUtilities.HasValidCoordinates(a_y, a_x))){
			return Utilities.ZERO;
		}
		
	    if(a_piece.IsPawn()){
	        return PAWN_VALUE + (a_piece.IsWhite() ? MoveEvaluation.m_whitePawnPositions[a_y][a_x] : MoveEvaluation.m_blackPawnPositions[a_y][a_x]);
	    }else if (a_piece.IsRook()){
	        return ROOK_VALUE + (a_piece.IsWhite() ? MoveEvaluation.m_whiteRookPositions[a_y][a_x] : MoveEvaluation.m_blackRookPositions[a_y][a_x]);
	    }else if (a_piece.IsKnight()){
	        return BISHOP_OR_KNIGHT_VALUE + MoveEvaluation.m_knightPositions[a_y][a_x];
	    }else if (a_piece.IsBishop()){
	        return BISHOP_OR_KNIGHT_VALUE + (a_piece.IsWhite() ? MoveEvaluation.m_whiteBishopPositions[a_y][a_x] : MoveEvaluation.m_blackBishopPositions[a_y][a_x]);
	    }else if (a_piece.IsQueen()){
	        return QUEEN_VALUE + MoveEvaluation.m_queenPositions[a_y][a_x];
	    }else if (a_piece.IsKing()){
	        return KING_VALUE + (a_piece.IsWhite() ? MoveEvaluation.m_whiteKingPositions[a_y][a_x] : MoveEvaluation.m_blackKingPositions[a_y][a_x]);
	    }
	        
	    return Utilities.ZERO;
	}

	/**/
    /*
    NAME
        public static Board MakeMove(final Board a_board, final Move a_move, final Player a_white, final Player a_black);
    
    SYNOPSIS
        public static Board MakeMove(final Board a_board, final Move a_move, final Player a_white, final Player a_black);
    
        Board a_board ------------> The board.
        
        Move a_move --------------> The move to be made.
        
        Player a_white -----------> The white player.
        
        Player a_black -----------> The black player.
       
    DESCRIPTION
        This method makes any type of move on the given board.

    RETURNS
        The board with the newly-made move.
    
    AUTHOR
        Ryan King
    */
	public static Board MakeMove(final Board a_board, final Move a_move, final Player a_white, final Player a_black){
		if(a_move.IsEnPassant()){
			return a_board.EnPassant((EnPassantMove)a_move, a_white, a_black);
		}else if(a_move.IsCastling()){
			return a_board.Castle((CastlingMove)a_move);
		}else if(a_move.IsAttacking()){
			return a_board.Attack((AttackingMove)a_move, a_white, a_black);
		}else{
			return a_board.Move((RegularMove)a_move);
		}
	}
	
	/**/
    /*
    NAME
        public static int Sort(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory);
    
    SYNOPSIS
        public static int Sort(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory);
    
    	Player a_player ----------> The current player.
    	
    	Player a_opponent --------> The opponent.
    
        Board a_board ------------> The board.
        
        int a_depth --------------> The search depth.
        
        String a_moveHistory -----> The player's move history in algebraic notation.

       
    DESCRIPTION
        This method sorts the ArrayList of moves by ascending "priority", that is,
        moves that put the opponent's king into checkmate or check are placed first,
        as they are the most important.
        Attacking moves go next, then castling moves, then regular moves.

    RETURNS
        ArrayList<Move> sortedMoves: The list of sorted moves.
    
    AUTHOR
        Ryan King
    */
	public static ArrayList<Move> Sort(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth){
		// moves will contain the moves a player can make
		ArrayList<Move> moves = new ArrayList<>();

		// Add the moves in order of priority
		moves.addAll(a_player.CheckmateMoves());
		moves.addAll(a_player.CheckMoves());
		moves.addAll(a_player.AttackingMoves());
		moves.addAll(a_player.CastlingMoves());
		moves.addAll(a_player.RegularMoves());
		moves.addAll(a_player.EnPassantMoves());
		
		// Get rid of duplicates in the list
		LinkedHashSet<Move> set = new LinkedHashSet<>(moves);
		
		// Make a new list with no duplicates
		ArrayList<Move> sortedMoves = new ArrayList<>(set);
		
		return sortedMoves;
	}
}