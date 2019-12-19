package com.DarkBlue.Testing;

import java.util.*;

import javax.swing.JOptionPane;

import com.DarkBlue.Board.*;
import com.DarkBlue.Game.*;
import com.DarkBlue.GUI.*;
import com.DarkBlue.Move.*;
import com.DarkBlue.Piece.*;
import com.DarkBlue.Player.*;
import com.DarkBlue.Utilities.*;

public class GameTest{
	
	private static Board m_board;
	private static Player m_white;
	private static Player m_black;
	private static Player m_currentPlayer;
	private static Player m_computerPlayer;
	// The exact coordinates the player chooses when moving
	private static int m_sourceRow;
	private static int m_sourceColumn;
	private static int m_destinationRow;
	private static int m_destinationColumn;
	
	private static Scanner m_keyboard;

	// The number of moves made; useful for determining draws
	private static int m_moves = Utilities.ZERO;
			
	// Search depth for the AI
	//private static int m_depth = Utilities.FIVE;// To be used later on
			
	// The integer that keeps track of the button
	private static int m_buttonInt;
			
	// The piece to be moved
	private static Piece m_candidate;
			
	// The victim of the move, if any
	private static Piece m_victim;
			
	// The piece that was moved previously. Useful in determining en passant for pawns
	private static Piece m_previouslyMoved;
			
	// The move to be made once evaluated
	private static Move m_nextMove;
	
	private static GameState m_state;
	
	private static ChessColor m_humanColor;

	public GameTest() {
		
	}
	
	/*
	NAME
		private final void CheckForPromotions(final Player a_player);
	
	SYNOPSIS
		private final void CheckForPromotions(final Player a_player);
	
		Player a_player ------> The player whose turn it is.
	
	DESCRIPTION
		This method checks the player's final rank to determine if 
		any of his/her pawns can get promoted. If a pawn is found,
		the method determines if it is on the last possible rank.
		If so, this pawn is promoted to a knight, bishop, rook, or queen,
		which is solely up to the discretion of the player, though 90% of
		promotions typically end with a queen. A new board gets generated 
		with the newly promoted piece in place of the pawn.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	private static final void CheckForPromotions(final Player a_player){
		ArrayList<Piece> activePiecesCopy = new ArrayList<>();
		for(int index = Utilities.ZERO; index < a_player.GetActivePieces().size(); index++){
			
			Piece piece = a_player.GetActivePieces().get(index);
			
			switch(piece.GetPieceType()){
				case PAWN: piece = new Pawn(piece, piece.GetCurrentRow(), piece.GetCurrentColumn(), piece.HowManyMoves());
				break;
				case ROOK: piece = new Rook(piece, piece.GetCurrentRow(), piece.GetCurrentColumn(), piece.HowManyMoves());
				break;
				case KNIGHT: piece = new Knight(piece, piece.GetCurrentRow(), piece.GetCurrentColumn(), piece.HowManyMoves());
				break;
				case BISHOP: piece = new Bishop(piece, piece.GetCurrentRow(), piece.GetCurrentColumn(), piece.HowManyMoves());
				break;
				case QUEEN: piece = new Queen(piece, piece.GetCurrentRow(), piece.GetCurrentColumn(), piece.HowManyMoves());
				break;
				case KING: piece = new King(piece, piece.GetCurrentRow(), piece.GetCurrentColumn(), piece.HowManyMoves());
				break;
				default: piece = null;
				break;
			}
			
			activePiecesCopy.add(piece);
		}
		
		Pawn pawn = null;
		// Check through all the player's pieces
		for(int index = Utilities.ZERO; index < activePiecesCopy.size(); index++){
			// Look for a pawn that's on its last rank
			if(activePiecesCopy.get(index).IsPawn()){
				if((a_player.IsWhite() && activePiecesCopy.get(index).GetCurrentRow() == Utilities.ZERO) || (a_player.IsBlack() && activePiecesCopy.get(index).GetCurrentRow() == Utilities.SEVEN)){
					pawn = (Pawn) a_player.GetActivePieces().get(index);
					// Return a new Board object with the new powerful piece replacing the pawn
					m_board = pawn.Promote(m_board);
					return;
				}
			}else{
				continue;
			}
		}
	}
	
	/*
	NAME
		public final boolean CheckSourceTile(final Delta a_source);
	
	SYNOPSIS
		public final boolean CheckSourceTile(final Delta a_source);
	
		Delta a_source --------> The pair of integers that represent the source tile.
	
	DESCRIPTION
		This method determines if the source coordinates entered in by the user
		are valid. If they are, they will be officially assigned to the variables
		and the method will return true.
		If not, they will not be assigned and the method will return false.
	
	RETURNS
		True if the coordinates are valid, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public static final void CheckSourceTile(){
		do{
			System.out.print(DarkBlue.SOURCE + ": ");
			String tile = m_keyboard.nextLine();
			m_sourceRow = Utilities.ToBoardRow(tile);
			m_sourceColumn = Utilities.ToBoardColumn(tile);
		try{
			if(!Utilities.HasValidCoordinates(m_sourceRow, m_sourceColumn)){
				JOptionPane.showMessageDialog(null, DarkBlue.INVALID_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
			}else{
			
				Piece mover = m_board.GetTile(m_sourceRow, m_sourceColumn).GetPiece();
			
				if(m_board.GetTile(m_sourceRow, m_sourceColumn).IsEmpty()){
					JOptionPane.showMessageDialog(null, DarkBlue.EMPTY_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				}else if(mover.GetColor().IsEnemy(m_currentPlayer.GetColor())){
					JOptionPane.showMessageDialog(null, DarkBlue.WRONG_COLOR, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				}else if(mover.GetColor().IsAlly(m_currentPlayer.GetColor()) && !mover.CanMove()){
					JOptionPane.showMessageDialog(null, DarkBlue.NO_LEGAL_MOVES, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				}
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, DarkBlue.INVALID_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
		}
		}while(!Utilities.HasValidCoordinates(m_sourceRow, m_sourceColumn));
	}
	
	/*
	NAME
		public final boolean CheckDestinationTile(final Delta a_destination);
	
	SYNOPSIS
		public final boolean CheckDestinationTile(final Delta a_destination);
	
		Delta a_destination --------> The pair of integers that represent the destination tile.
	
	DESCRIPTION
		This method determines if the destination coordinates entered in by the user
		are valid. If they are, they will be officially assigned to the variables
		and the method will return true.
		If not, they will not be assigned and the method will return false.
	
	RETURNS
		True if the coordinates are valid, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public static final void CheckDestinationTile(){
		do{
			System.out.print(DarkBlue.DESTINATION + ": ");
			String tile = m_keyboard.nextLine();
			m_destinationRow = Utilities.ToBoardRow(tile);
			m_destinationColumn = Utilities.ToBoardColumn(tile);
		try{
			if(!Utilities.HasValidCoordinates(m_destinationRow, m_destinationColumn)){
				JOptionPane.showMessageDialog(null, DarkBlue.INVALID_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
			}else{
				
				Piece mover = m_board.GetTile(m_sourceRow, m_sourceColumn).GetPiece();
				Piece victim = m_board.GetTile(m_destinationRow, m_destinationColumn).GetPiece();
			
				if(!Utilities.IsLegal(mover, m_destinationRow, m_destinationColumn)){
					JOptionPane.showMessageDialog(null, DarkBlue.ILLEGAL_MOVE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				}else if(victim != null && victim.IsAlly(mover)){
					JOptionPane.showMessageDialog(null, DarkBlue.OWN_COLOR, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				}else if(victim != null && victim.IsEnemy(mover) && victim.IsKing()){
					JOptionPane.showMessageDialog(null, DarkBlue.KING, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				}
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, DarkBlue.INVALID_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
		}
		}while(!Utilities.HasValidCoordinates(m_destinationRow, m_destinationColumn));
	}
	
	/*
	NAME
		private final void EvaluateGameState();
	
	SYNOPSIS
		private final void EvaluateGameState();
		
		No parameters.
	
	DESCRIPTION
		This method checks the state of the game.
		It sets the m_state variable to one of the following:
		
		GameState.CHECK: One player's king is in check. He must either move out of it,
		capture the threatening piece himself, use a friendly piece to 
		capture the threatening piece, or use a friendly piece to 
		block the check, assuming the threatening piece moves linearly.
		The game resumes as normal after the player removes the threat.
		
		GameState.STALEMATE: One player's king is not in check but has no legal moves.
		None of the player's other pieces have any legal moves either. 
		The game ends in a draw.
		
		GameState.CHECKMATE: One player's king is in check 
		in his current position and wherever he moves.
		He cannot escape, he cannot capture the piece(s) putting him into check,
		no other friendly piece can capture the piece(s) putting him into check,
		and no friendly piece can block the check. 
		Any other friendly pieces with moves that would not help the king are not allowed to move.
		The other player wins and the game is over.
		
		GameState.DRAW: The game can end with no winner due to a number of conditions:
		
			1. White and black have only bare kings left on the board.
		
			2. One player has a king and the other has a king and a bishop.
		
			3. One player has a king and the other has a king and a knight.
		
			4. Both players have a king and a bishop and both bishops 
				move on the same tile color.
		
		5. The same board configuration has been repeated 3 times. 
			This is typically a symptom of continually putting the opponent's king into check.
			
		6. Fifty moves have been made without a pawn movement or capture.
		
		GameState.NORMAL: The game proceeds as normal.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public static final GameState EvaluateGameState(){
		if(m_currentPlayer.IsInCheck(m_board)){
			return GameState.CHECK;
		}else if(m_currentPlayer.IsInStalemate(m_board)){
			return GameState.STALEMATE;
		}else if(m_currentPlayer.IsInCheckmate(m_board)){
			return GameState.CHECKMATE;
		}else if(IsDrawByInsufficientMaterial() || IsDrawByFiftyMoveRule() || IsDrawByThreefoldRepetition()){
			return GameState.DRAW;
		}else{
			return GameState.NORMAL;
		}
	}
	
	/*
	NAME
		private final void AssignCurrentPlayer();
	
	SYNOPSIS
		private final void AssignCurrentPlayer();
	
		No parameters.
	
	DESCRIPTION
		This method assigns the current player.
		This is done to reduce code later on and
		give a convenient way to address the player
		whose turn it is.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	private static final void AssignCurrentPlayer(){
		if(m_board.WhoseTurnIsIt().IsWhite()){
			m_currentPlayer = m_white;
		}else{
			m_currentPlayer = m_black;
		}
	}
	
	/*
	NAME
		private final boolean IsDrawByInsufficientMaterial();
	
	SYNOPSIS
		private final boolean IsDrawByInsufficientMaterial();
	
		No parameters.
	
	DESCRIPTION
		This method checks to see if any draw condition by
		insufficient material exists. 
		This can occur in one of several ways:
		
		1. Both sides have bare kings. Since kings cannot be captured
		nor give check, the game ends in a draw.
		
		2. One side has a bare king and the other has a king and a knight.
		
		3. One side has a bare king and the other has a king and a bishop.
		
		4. Both sides have a king and a bishop and the bishops are on tiles
		of the same color.
		
		If any of these conditions are met, the method returns true.
		If not, it returns false.
	
	RETURNS
		True if the game is drawn by any of the preceding non-stalemate conditions
		and false otherwise. One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	private static final boolean IsDrawByInsufficientMaterial(){
		if(m_white.HasBareKing() && m_black.HasBareKing()){
			return true;
		}else if(m_white.HasBareKing() && m_black.HasKingAndBishop()){
			return true;
		}else if(m_white.HasBareKing() && m_black.HasKingAndKnight()){
			return true;
		}else if(m_black.HasBareKing() && m_white.HasKingAndBishop()){
			return true;
		}else if(m_black.HasBareKing() && m_white.HasKingAndKnight()){
			return true;
		}else if(m_white.HasKingAndBishop() && m_black.HasKingAndBishop()){
			int whiteBishopRow = Utilities.NEGATIVE_ONE, whiteBishopColumn = Utilities.NEGATIVE_ONE, 
					blackBishopRow = Utilities.NEGATIVE_ONE, blackBishopColumn = Utilities.NEGATIVE_ONE;
			for(int index = Utilities.ZERO; index < Utilities.TWO; index++){
				if(m_white.GetActivePieces().get(index).IsBishop()){
					whiteBishopRow = m_white.GetActivePieces().get(index).GetCurrentRow();
					whiteBishopColumn = m_white.GetActivePieces().get(index).GetCurrentColumn();
				}
				if(m_black.GetActivePieces().get(index).IsBishop()){
					blackBishopRow = m_black.GetActivePieces().get(index).GetCurrentRow();
					blackBishopColumn = m_black.GetActivePieces().get(index).GetCurrentColumn();
				}
			}
			if(m_board.GetTile(whiteBishopRow, whiteBishopColumn).GetColor() == m_board.GetTile(blackBishopRow, blackBishopColumn).GetColor()){
				return true;
			}
		}
		return false;
	}
	
	/*
	NAME
		private final boolean IsDrawByFiftyMoveRule();
	
	SYNOPSIS
		private final boolean IsDrawByFiftyMoveRule();
	
		No parameters.
	
	DESCRIPTION
		This method checks to see if any draw condition by
		the fifty-move rule exists.
		
		This occurs when fifty moves have been made without a 
		single pawn movement or capture of any kind.
		
		If this condition is met, the method returns true.
		If not, it returns false.
	
	RETURNS
		True if the game is drawn by any of the preceding non-stalemate conditions
		and false otherwise. One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	private static final boolean IsDrawByFiftyMoveRule(){
		return m_moves >= Utilities.FIFTY;
	}
	
	/*
	NAME
		private final boolean IsDrawByThreefoldRepetition();
	
	SYNOPSIS
		private final boolean IsDrawByThreefoldRepetition();
	
		No parameters.
	
	DESCRIPTION
		This method checks to see if any draw condition by
		threefold repetition exists.
		
		This occurs when the same three moves have occurred
		consecutively; typically this happens when one player's
		king is put into check many times.
		
		If this condition is met, the method returns true.
		If not, it returns false.
	
	RETURNS
		True if the game is drawn by any of the preceding non-stalemate conditions
		and false otherwise. One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	private static final boolean IsDrawByThreefoldRepetition(){
		return false;
	}
	
	/*
	NAME
		private final boolean IsEnPassantMove();
	
	SYNOPSIS
		private final boolean IsEnPassantMove();
	
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
	private static final boolean IsEnPassantMove(){
		/*
		return (((m_candidate.IsWhite() && m_destinationRow == m_startRow - Utilities.ONE) 
				|| (m_candidate.IsBlack() && m_destinationRow == m_startRow + Utilities.ONE)) 
				&& (m_destinationColumn == m_startColumn + Utilities.ONE || m_destinationColumn == m_startColumn - Utilities.ONE) 
				&& (Utilities.HasValidCoordinates(m_destinationRow, m_startRow - Utilities.ONE)
						|| Utilities.HasValidCoordinates(m_destinationRow, m_startRow + Utilities.ONE))
				&& m_board.GetTile(m_destinationRow, m_destinationColumn).IsEmpty()
				&& (m_previouslyMoved.IsPawn() 
				&& m_previouslyMoved.IsEnemy(m_candidate)
				&& m_previouslyMoved.HowManyMoves() == Utilities.ONE
				&& Math.abs(m_previouslyMoved.GetCurrentRow() - m_oldRow) == Utilities.TWO
				&& m_oldColumn == m_previouslyMoved.GetCurrentColumn()));
		*/
		return m_victim != null
					&& m_victim.IsPawn() 
					&& m_victim.Equals(m_previouslyMoved) 
					&& (m_victim.GetCurrentRow() == m_destinationRow - Utilities.ONE
							|| m_victim.GetCurrentRow() == m_destinationRow + Utilities.ONE)
					&& m_victim.GetCurrentColumn() == m_destinationColumn;
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
	private static final boolean IsCastlingMove(){
		return m_destinationRow == m_sourceRow 
				&& (m_destinationColumn == m_sourceColumn + Utilities.TWO 
				|| m_destinationColumn == m_sourceColumn - Utilities.TWO);
	}
	
	/*
	NAME
		public final Move EvaluateMove();
	
	SYNOPSIS
		public final Move EvaluateMove();
	
		No parameters.
	
	DESCRIPTION
		This method creates a new Move object.
		Depending on the type of move this is, it could be:
		
		A regular move 
		(Any piece moving to an empty tile),
		
		An attacking move 
		(Any piece moving to an occupied tile and
		capturing the opposing piece on that tile the way it typically captures),
		
		A castling move 
		(Swapping the king and the rook when there are no other pieces between them,
		the king will not move through check and neither the
		king nor the rook has moved yet), or
		
		An en passant move
		(A special attacking move which can only occur if a pawn is at its fifth rank
		and the previous piece to move was an opposing pawn that advanced 2 squares on
		its first move and could have been taken by the other pawn had it only moved 1 square.
		This is the only legal move where the destination tile is not the same as the tile
		of the captured piece).
	
	RETURNS
		Move move: The evaluated move, ready to be made.
		This will never return null, because at this point,
		all possible moves it could generate are deemed to be legal.
	
	AUTHOR
		Ryan King
	*/
	private static final Move EvaluateMove(){
		Move move;
		// Instantiate the desired move
		if(!m_candidate.IsKing() && !m_candidate.IsPawn()){// This is definitely not a castling or en passant move
			if(m_victim != null){
				move = new AttackingMove(m_candidate, m_destinationRow, m_destinationColumn, m_victim);
			}else{
				move = new RegularMove(m_candidate, m_destinationRow, m_destinationColumn);
			}
		}else{
			if(m_candidate.IsKing()){// This could be a castling move
				if(IsCastlingMove()){
					// This is a castling move
					move = new CastlingMove((King)m_candidate, m_destinationRow, m_destinationColumn);
					
					//m_board.GetTile(((CastlingMove) move).GetRookCurrentRow(), ((CastlingMove) move).GetRookCurrentColumn()).GetPiece().IncrementMoves();					
				}else{// This is a regular or attacking move
					if(m_victim != null){
						move = new AttackingMove(m_candidate, m_destinationRow, m_destinationColumn, m_victim);
					}else{
						move = new RegularMove(m_candidate, m_destinationRow, m_destinationColumn);
					}
				}
			}else{// This could be a regular move, an attacking move, or an en passant move
				if(IsEnPassantMove()){
					// This is an en passant move
					Pawn victim;
						if(Utilities.HasValidCoordinates(m_candidate.GetCurrentRow(), m_candidate.GetCurrentColumn() + Utilities.ONE)
								&& m_board.GetTile(m_candidate.GetCurrentRow(), m_candidate.GetCurrentColumn() + Utilities.ONE).IsOccupied()){
							victim = (Pawn) m_board.GetTile(m_sourceRow, m_sourceColumn + Utilities.ONE).GetPiece();
						}else{
							victim = (Pawn) m_board.GetTile(m_sourceRow, m_sourceColumn - Utilities.ONE).GetPiece();
						}
					move = new EnPassantMove((Pawn)m_candidate, m_destinationRow, m_destinationColumn, victim);
				}else{// This isn't an en passant move
					// This is a regular or attacking move
					if(m_victim != null){
						move = new AttackingMove(m_candidate, m_destinationRow, m_destinationColumn, m_victim);
					}else{
						move = new RegularMove(m_candidate, m_destinationRow, m_destinationColumn);
					}
				}
			}
		}
		// Record that the piece moved
		// ?

		//EvaluatePreviouslyMoved();
		
		// Return the complete move
		return move;
	}
	
	/*
	NAME
		public final void EvaluatePreviouslyMoved();
	
	SYNOPSIS
		public final void EvaluatePreviouslyMoved();
	
		No parameters.
	
	DESCRIPTION
		This method sets the m_previouslyMoved field
		to a deep copy of the piece that just moved.
		This is useful for determining en passant moves.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	/*
	public static final void EvaluatePreviouslyMoved(){
		// Make a deep copy of the piece that just moved
		switch(m_candidate.GetPieceType()){
			case PAWN: m_previouslyMoved = new Pawn(m_candidate);
			break;
			case ROOK: m_previouslyMoved = new Rook(m_candidate);
			break;
			case KNIGHT: m_previouslyMoved = new Knight(m_candidate);
			break;
			case BISHOP: m_previouslyMoved = new Bishop(m_candidate);
			break;
			case QUEEN: m_previouslyMoved = new Queen(m_candidate);
			break;
			case KING: m_previouslyMoved = new King(m_candidate);
			break;
			default: m_previouslyMoved = null;
			break;
		}
	}
	*/
	
	/*
	NAME
		private final void MakeMove();
	
	SYNOPSIS
		private final void MakeMove();
	
		No parameters.
	
	DESCRIPTION
		This method makes the desired
		move on the board using the
		appropriate method in the Board class.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	private static final void MakeMove(){
		if(m_nextMove.IsAttacking()){
			m_board = m_board.Attack((AttackingMove)m_nextMove, m_white, m_black);
		}else if(m_nextMove.IsCastling()){
			m_board = m_board.Castle((CastlingMove)m_nextMove);
		}else if(m_nextMove.IsEnPassant()){
			m_board = m_board.EnPassant((EnPassantMove)m_nextMove, m_white, m_black);
		}else{
			m_board = m_board.Move((RegularMove)m_nextMove);
		}
	}
	
	private static final void InitializePlayers(final Board a_board){
		if(m_humanColor.IsWhite()){// Timeout exception?
			m_white = new Human(ChessColor.WHITE, a_board);
			m_black = new Computer(ChessColor.BLACK, a_board);
			m_computerPlayer = m_black;
		}else{
			m_white = new Computer(ChessColor.WHITE, a_board);
			m_black = new Human(ChessColor.BLACK, a_board);
			m_computerPlayer = m_white;
		}
		RefreshPlayers();
	}
	
	/*
	NAME
		public final void RefreshPlayers();
	
	SYNOPSIS
		public final void RefreshPlayers();
	
		No parameters.
	
	DESCRIPTION
		This method refreshes the pieces
		and legal moves of both players.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public static final void RefreshPlayers(){
		m_white.Refresh(m_board);
		m_black.Refresh(m_board);
	}
	
	public static final void PrintBoard(){
		if(m_board.WhoseTurnIsIt().IsWhite()){
			System.out.println(m_board.GetWhiteBoard());
		}else{
			System.out.println(m_board.GetBlackBoard());
		}
	}
	
	/*
	NAME
		private final void ComputerPlay(final Computer a_computer);
	
	SYNOPSIS
		private final void ComputerPlay(final Computer a_computer);
	
		Computer a_computer ----> The computer player.
	
	DESCRIPTION
		This method enables the computer player to
		make a move with the minimax algorithm.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	private static final void ComputerPlay(final Computer a_computer){
		ArrayList<Move> allPossibleMoves = a_computer.UglyMoves();
		
		// Make a stupid random AI for now
		
		// Get any old move
		Random random = new Random();
		int move = random.nextInt(allPossibleMoves.size());
		
		// Get the moving piece
		m_nextMove = allPossibleMoves.get(move);// For the stupid AI
		//m_nextMove = a_computer.Search(Utilities.ZERO);// For a slightly better AI
		
		// Get the piece's old coordinates
		m_sourceRow = m_nextMove.GetOldRow();
		m_sourceColumn = m_nextMove.GetOldColumn();
		
		// Get the piece's new coordinates
		m_destinationRow = m_nextMove.GetNewRow();
		m_destinationColumn = m_nextMove.GetNewColumn();
		
		// Get the victim the piece is capturing, if any
		m_victim = m_nextMove.GetVictim();
		
		// Make the move
		MakeMove();
		
		//EvaluatePreviouslyMoved();
		
		if(m_board.WhoseTurnIsIt() == ChessColor.WHITE){
			CheckForPromotions(m_black);
		}else{
			CheckForPromotions(m_white);
		}
		
		RefreshPlayers();
	}
	
	public static final void HumanPlay(){
		CheckSourceTile();
		
		CheckDestinationTile();
		
		// Initialize the move
		m_nextMove = EvaluateMove();
		
		// Reset the move count if a pawn gets moved or a piece gets captured
		if(m_candidate.IsPawn() || m_victim != null){
			m_moves = Utilities.ZERO;
		}else{
			m_moves++;
		}
	
		// Make the move
		MakeMove();
		
		// Take note of the previously moved piece
		//EvaluatePreviouslyMoved();
		
		// Check for promotions on the side that's moving next
		if(m_board.WhoseTurnIsIt() == ChessColor.WHITE){
			CheckForPromotions(m_black);
		}else{
			CheckForPromotions(m_white);
		}
				
		// Refresh pieces and legal moves for both sides
		RefreshPlayers();
	}
	
	/*
	NAME
		private final void ChooseColor();
	
	SYNOPSIS
		private final void ChooseColor();
	
		No parameters.
	
	DESCRIPTION
		This method allows the human player
		to choose his/her desired color, which
		can either be black or white.
		The computer takes the other color.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	private static final void ChooseColor(){
		
		Object[] colors = {DarkBlue.BLACK, DarkBlue.WHITE};
		
		while(true){
			
			m_buttonInt = JOptionPane.showOptionDialog(null, DarkBlue.PLAY_AS, DarkBlue.TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, colors, null);
			
			switch(m_buttonInt){
				// For a black human player
				case Utilities.ZERO: m_humanColor = ChessColor.BLACK;
				break;
				// For a white human player
				case Utilities.ONE:  m_humanColor = ChessColor.WHITE;
				break;
				// Do not allow the player to proceed without choosing a color
				default: continue;
			}
			m_board = Board.GetStartingPosition();
			
			InitializePlayers(m_board);

			break;
		}
	}

	public static final void main(String a_args){
		m_keyboard = new Scanner(System.in);
		
		ChooseColor();
		// Only continue for as long as both kings are safe or one player is in check
		while(true){// Beginning of while loop
					
			// Determine whose turn it is and assign the alias accordingly
			AssignCurrentPlayer();
					
			// Determine the state of the game after the newly-made move
			m_state = EvaluateGameState();
					
			// Check to see if either player is in check, stalemate,
			// or checkmate and handle those situations appropriately
			if(m_state == GameState.CHECK && m_currentPlayer.IsHuman()){
				JOptionPane.showMessageDialog(null, DarkBlue.CHECK_MESSAGE, DarkBlue.TITLE, JOptionPane.WARNING_MESSAGE);
				// Just display this message and do not return; the game is still playable
			}else if(m_state == GameState.STALEMATE){
				JOptionPane.showMessageDialog(null, DarkBlue.STALEMATE_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}else if(m_state == GameState.DRAW && IsDrawByFiftyMoveRule()){
				JOptionPane.showMessageDialog(null, DarkBlue.FIFTY_MOVE_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}else if(m_state == GameState.DRAW && IsDrawByInsufficientMaterial()){
				JOptionPane.showMessageDialog(null, DarkBlue.INSUFFICIENT_MATERIAL_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}else if(m_state == GameState.DRAW && IsDrawByThreefoldRepetition()){
				JOptionPane.showMessageDialog(null, DarkBlue.THREEFOLD_REPETITION_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}else if(m_state == GameState.CHECKMATE && m_currentPlayer.IsWhite()){
				JOptionPane.showMessageDialog(null, DarkBlue.BLACK_CHECKMATE_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}else if(m_state == GameState.CHECKMATE && m_currentPlayer.IsBlack()){
				JOptionPane.showMessageDialog(null, DarkBlue.WHITE_CHECKMATE_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}
					
			RefreshPlayers();
					
			PrintBoard();
					
			m_board.PrintWhoseTurn();
					
			if(m_currentPlayer.IsHuman()){
				HumanPlay();					
			}else{
				System.out.println("Thinking...");
				ComputerPlay((Computer)m_computerPlayer);
			}
					
		}// End of while true game loop
	}
}
