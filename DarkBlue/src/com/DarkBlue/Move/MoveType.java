package com.DarkBlue.Move;

/**
 * This represents the type of moves that exist in chess.
 * 
 * All names are self-explanatory.
 * 
 * Regular moves involve any piece moving anywhere without capturing anything.
 * 
 * Attacking move involve any piece moving anywhere and capturing a victim.
 * 
 * Castling moves involve the king moving 2 spaces to the left or right and
 * swapping places with the rook on that side.
 * 
 * En passant moves involve a pawn on its fifth rank capturing an enemy pawn
 * that just moved two tiles on its first move on the previous turn.
 */
public enum MoveType{
    REGULAR, 
    ATTACKING, 
    CASTLING, 
    EN_PASSANT;
}