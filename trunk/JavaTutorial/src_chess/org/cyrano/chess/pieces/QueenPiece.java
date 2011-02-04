/*
 * Copyright 2004 Minotauro C.A. Reservados todos los derechos.
 * Created on Oct 24, 2003
 */
package org.cyrano.chess.pieces;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author DMI: Demian Gutierrez
 */
public class QueenPiece extends AbstractPiece
{
	protected static Image whiteImage;

	protected static Image blackImage;

	/**
	 * 
	 */
	static {

		try
		{
			whiteImage = ImageIO.read(ClassLoader.getSystemResourceAsStream("org/cyrano/chess/images/WhiteQueen.png"));
			blackImage = ImageIO.read(ClassLoader.getSystemResourceAsStream("org/cyrano/chess/images/BlackQueen.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public QueenPiece(int currI, int currJ)
	{
		super(currI, currJ);
	}

	// --------------------------------------------------------------------------------
	// AbstractPiece
	// --------------------------------------------------------------------------------

	/**
	 * 
	 */
	public Image getImage()
	{
		return color == COLOR_WHITE ? whiteImage : blackImage;
	}

	// --------------------------------------------------------------------------------

	/**
	 * 
	 */
	public boolean canMoveTo(int i, int j, AbstractPiece[][] board)
	{
		return bishopMove(i, j, board, false) || castleMove(i, j, board, false);
	}

	/**
	 * 
	 */
	public AbstractPiece captureTo(int i, int j, AbstractPiece[][] board)
	{
		return board[i][j];
	}
}
