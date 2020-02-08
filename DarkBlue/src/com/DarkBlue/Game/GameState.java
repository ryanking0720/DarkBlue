package com.DarkBlue.Game;

/*
    The GameState enum
  
    This enumeration represents the possible states of the chessboard
    during a game of chess:
   
    GameState.CHECK: One player's king is in check. 
    That player must perform one of the following options on his/her next turn:
    
        1. Move his/her king to a safe tile, as dictated by his legal moves (if any),
    
        2. Capture the threatening piece with the king (if possible), 
    
        3. Use a friendly piece to capture the threatening piece, or 
        
        4. Use a friendly piece to block the threat, 
        assuming the threatening piece moves linearly
        (e.g. if it's a rook, bishop, or queen).
        
    All other moves that are otherwise legal 
    but do not take the king out of check are deemed illegal.
    This includes castling, even if the other conditions of castling have been met.
    The threat must be removed immediately before play can continue.
    The game resumes as normal after the player removes the threat,
    and any moves that were previously illegal as explained above
    are deemed legal again.
        
    GameState.STALEMATE: One player's king is in stalemate.
    This means that the player's king is not in check but has no legal moves.
    None of the player's other pieces (if any) have any legal moves either. 
    The game ends in a draw.
        
    GameState.CHECKMATE: One player's king is in checkmate.
    This is the ultimate objective in the game of chess.
    To deliver checkmate to one's opponent 
    means the delivering player wins the game.

    All of the following conditions are true:
    
        1. The player's king is in check,
    
        2. He cannot escape, 
    
        3. He cannot capture the piece(s) threatening him,
    
        4. No other friendly piece can capture the threatening piece(s), and
        
        5. No other friendly piece can block the check 
        if the attacking piece moves linearly. 
    
    As with the state of ordinary check,
    any moves that would not help the king are deemed illegal.
    The player placed in checkmate loses the game.
        
    GameState.DRAW: The game can end with no winner due to a number of conditions:
        
        1. Both players have only bare kings left on the board,
        
        2. One player has a king and the other has a king and a bishop,
        
        3. One player has a king and the other has a king and a knight,
        
        4. Both players have a king and a bishop and both bishops 
           move on the same tile color, or
        
        5. The same board configuration has been repeated 3 times. 
           This is typically a symptom of continually putting the opponent's king into check,
           which is referred to as "perpetual check".
            
        6. Fifty halfmoves have been made without a pawn movement or capture.
        
    GameState.NORMAL: Neither king is threatened. The game proceeds as normal.
    
    GameState.EMPTY: The program is initialized and the board is empty. Nothing happens.
 */
public enum GameState{
    NORMAL,
    CHECK,
    CHECKMATE,
    STALEMATE,
    DRAW,
    EMPTY;
}