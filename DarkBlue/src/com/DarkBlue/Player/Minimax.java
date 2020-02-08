package com.DarkBlue.Player;

import com.DarkBlue.Board.Board;
import com.DarkBlue.GUI.DarkBlue;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Factory;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Move.Move;
import com.DarkBlue.Move.RegularMove;
import com.DarkBlue.Move.AttackingMove;
import com.DarkBlue.Move.CastlingMove;
import com.DarkBlue.Move.EnPassantMove;

import java.util.ArrayList;
import java.util.Random;

public class Minimax{
	
	public static final int CHECK_FACTOR = 50;
	public static final int CHECKMATE_FACTOR = 10000;
	public static final int DEPTH_FACTOR = 100;
	public static final int CASTLING_FACTOR = 60;
	public static final Random random = new Random();
	
	public static final int Minimize(final Board a_board, final Player a_white, final Player a_black, final int a_depth, final int a_highest, final int a_lowest){
		
		final Player mover = (a_board.WhoseTurnIsIt() == ChessColor.WHITE ? a_white : a_black);
		final Player opponent = (mover.IsWhite() ? a_black : a_white);
		final String history = (mover.IsWhite() ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory());
		
		if(a_depth == Utilities.ZERO){
			return EvaluatePlayer(mover, opponent, a_board, a_depth, history);
		}
		
		int lowestValue = a_lowest;
		
		ArrayList<Move> moves = mover.UglyMoves();
		
		for(Move move : moves){
			Board clone = Board.GetDeepCopy(a_board);
			Player tempWhite = (a_white.IsHuman() ? new Human(a_white, clone) : new Computer(a_white, clone));
			Player tempBlack = (a_black.IsHuman() ? new Human(a_black, clone) : new Computer(a_black, clone));

			clone = MakeMove(clone, move, tempWhite, tempBlack);
			
			tempWhite.Refresh(clone);
			tempBlack.Refresh(clone);
			
			final int currentValue = Maximize(clone, tempWhite, tempBlack, a_depth - Utilities.ONE, a_highest, lowestValue);
			
			if(currentValue < lowestValue){
				lowestValue = currentValue;
			}
			
			if(a_highest >= lowestValue){
				break;
			}
		}
		
		return lowestValue;
	}
	
	public static final int Maximize(final Board a_board, final Player a_white, final Player a_black, final int a_depth, final int a_highest, final int a_lowest){
		
		final Player mover = (a_board.WhoseTurnIsIt() == ChessColor.WHITE ? a_white : a_black);
		final Player opponent = (mover.IsWhite() ? a_black : a_white);
		final String history = (mover.IsWhite() ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory());
		
		if(a_depth == Utilities.ZERO){
			return EvaluatePlayer(mover, opponent, a_board, a_depth, history);
		}
		
		int highestValue = a_highest;
		
		ArrayList<Move> moves = mover.UglyMoves();
		
		for(Move move : moves){
			Board clone = Board.GetDeepCopy(a_board);
			Player tempWhite = (a_white.IsHuman() ? new Human(a_white, clone) : new Computer(a_white, clone));
			Player tempBlack = (a_black.IsHuman() ? new Human(a_black, clone) : new Computer(a_black, clone));

			clone = MakeMove(clone, move, tempWhite, tempBlack);
			
			tempWhite.Refresh(clone);
			tempBlack.Refresh(clone);
			
			final int currentValue = Minimize(clone, tempWhite, tempBlack, a_depth - Utilities.ONE, highestValue, a_lowest);			
			
			if(currentValue > highestValue){
				highestValue = currentValue;
			}
			
			if(a_lowest <= highestValue){
				break;
			}
		}
		
		return highestValue;
	}
	
	public static final Move Search(final Board a_board, final Player a_white, final Player a_black, final int a_depth){
		int highestValue = Integer.MIN_VALUE;
		int lowestValue = Integer.MAX_VALUE;
		int currentValue = Utilities.ZERO;
		
		Move bestMove = null;
		
		final Player currentPlayer = (a_board.WhoseTurnIsIt() == ChessColor.WHITE ? a_white : a_black);
		
		final ArrayList<Move> moves = Minimax.Sort((a_board.WhoseTurnIsIt() == ChessColor.WHITE ? a_white : a_black), (a_board.WhoseTurnIsIt() == ChessColor.WHITE ? a_black : a_white), a_board, a_depth, (a_board.WhoseTurnIsIt() == ChessColor.WHITE ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory()));
		
		final int numberOfMoves = moves.size();
		
		final ChessColor turn = a_board.WhoseTurnIsIt();
		
		for(int index = Utilities.ZERO; index < numberOfMoves; index++){
			final Move move = moves.get(index);
			
			Board clone = Board.GetDeepCopy(a_board);
			final Player tempWhite = (a_white.IsHuman() ? new Human(a_white, clone) : new Computer(a_white, clone));
			final Player tempBlack = (a_black.IsHuman() ? new Human(a_black, clone) : new Computer(a_black, clone));
			
			tempWhite.Refresh(clone);
			tempBlack.Refresh(clone);
			
			clone = MakeMove(clone, move, tempWhite, tempBlack);
			
			tempWhite.Refresh(clone);
			tempBlack.Refresh(clone);
			
			currentValue = (clone.WhoseTurnIsIt() == ChessColor.WHITE ? Minimize(clone, tempWhite, tempBlack, a_depth - Utilities.ONE, Integer.MIN_VALUE, Integer.MAX_VALUE) : Maximize(clone, tempWhite, tempBlack, a_depth - Utilities.ONE, Integer.MIN_VALUE, Integer.MAX_VALUE));
			
			if(turn == ChessColor.WHITE && currentValue > highestValue){
				highestValue = currentValue;
				bestMove = move;
				
				if(tempBlack.IsInCheckmate(clone)){
					break;
				}
			}else if(turn == ChessColor.BLACK && currentValue < lowestValue){
				lowestValue = currentValue;
				bestMove = move;
				
				if(tempWhite.IsInCheckmate(clone)){
					break;
				}				
			}
		}
		
		return bestMove;
	}
	
	public static final Board MakeMove(final Board a_board, final Move a_move, final Player a_white, final Player a_black){
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
	
	public static final int EvaluatePlayer(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory){
		return a_player.PieceValue() + a_player.HowManyMoves() + CheckFactor(a_opponent, a_board) 
		+ CheckmateFactor(a_opponent, a_board) + DepthFactor(a_depth) + CastlingFactor(a_player, a_moveHistory);
	}
	
	private static final int CheckFactor(final Player a_player, final Board a_board){
		if(a_player.IsInCheck(a_board)){
			return CHECK_FACTOR;
		}else{
			return Utilities.ZERO;
		}
	}
	
	public static final ArrayList<Move> Sort(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory){
		ArrayList<Move> moves = a_player.UglyMoves(), sortedMoves = new ArrayList<>();
		ArrayList<Integer> values = new ArrayList<>();

		int bestValue = EvaluatePlayer(a_player, a_opponent, a_board, a_depth, a_moveHistory);
		int currentValue = Utilities.ZERO;
		
		for(int i = Utilities.ZERO; i < moves.size(); i++){
			Board clone = Board.GetDeepCopy(a_board);
            Human tempWhite = new Human(ChessColor.WHITE, clone);
            Human tempBlack = new Human(ChessColor.BLACK, clone);
            tempWhite.InitializePieces(clone);
            tempBlack.InitializePieces(clone);
            Player mover;
            
            if(clone.WhoseTurnIsIt().IsWhite()){
                mover = tempWhite;
            }else{
                mover = tempBlack;
            }
            
            clone = Minimax.MakeMove(clone, moves.get(i), tempWhite, tempBlack);
            
            currentValue = EvaluatePlayer((a_board.WhoseTurnIsIt().IsWhite() ? tempBlack : tempWhite), (a_board.WhoseTurnIsIt().IsWhite() ? tempWhite : tempBlack), clone, a_depth, a_moveHistory);
                  
            values.add(currentValue);
		}
		
		int index;
		
		while(!moves.isEmpty()){
			index = (a_player.IsWhite() ? GetSmallestIndex(values) : GetLargestIndex(values));
			sortedMoves.add(Factory.MoveFactory(moves.get(index).GetPiece(), moves.get(index).GetNewRow(), moves.get(index).GetNewColumn(), moves.get(index).GetVictim(), a_board));
			moves.remove(index);
			values.remove(index);
			
		}
		
		return sortedMoves;
	}
	
	private static final int GetSmallestIndex(ArrayList<Integer> a_values){
		int smallest = Utilities.ZERO;
		
		for(int i = Utilities.ONE; i < a_values.size(); i++){
			if(a_values.get(i) < a_values.get(smallest)){
				smallest = i;
			}
		}
		
		return smallest;
	}
	
	private static final int GetLargestIndex(ArrayList<Integer> a_values){
		int largest = Utilities.ZERO;
		
		for(int i = Utilities.ONE; i < a_values.size(); i++){
			if(a_values.get(i) > a_values.get(largest)){
				largest = i;
			}
		}
		
		return largest;
	}
	
	private static final int CheckmateFactor(final Player a_player, final Board a_board){
		if(a_player.IsInCheckmate(a_board)){
			return CHECKMATE_FACTOR;
		}else{
			return Utilities.ZERO;
		}
	}
	
	private static final int DepthFactor(final int a_depth){
		if(a_depth == Utilities.ZERO){
			return Utilities.ONE;
		}else{
			return DEPTH_FACTOR * a_depth;
		}
	}
	
	private static final int CastlingFactor(final Player a_player, final String a_moveHistory){
		if(a_player.HasCastled(a_moveHistory)){
			return CASTLING_FACTOR;
		}else{
			return Utilities.ZERO;
		}
	}
}