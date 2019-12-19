package com.DarkBlue.Testing;

import org.junit.jupiter.api.Test;
import com.DarkBlue.Piece.*;
import com.DarkBlue.Utilities.*;
import static org.junit.Assert.*;

public class PieceTest{

	public PieceTest(){
		// TODO Auto-generated constructor stub
	}

	@Test
	public void Test(){
		Piece pawn1 = new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.FOUR);
		Piece pawn2 = new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.FOUR);
		assertEquals(pawn1, pawn2);
	}
}
