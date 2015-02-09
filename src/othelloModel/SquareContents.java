package othelloModel ;

/**
 * To represent the contents of a square on the Othello board.
 */ 
public enum SquareContents {
  EMPTY,
  WHITE_PIECE,
  BLACK_PIECE ;
	
  /**
   * If white, returns black and vice versa.
   */
  public SquareContents oppositeColour() {
    switch(this) {
    case WHITE_PIECE:
      return BLACK_PIECE ;
    case BLACK_PIECE:
      return WHITE_PIECE ;
    default:
      System.out.println("EMPTY doesn't have an opposite colour") ;
      return null ;
    }
  }

  /**
   * Returns true if this enum instance is a white piece
   */
  public boolean isWhite() { return this == WHITE_PIECE ; }

  /**
   * Returns true if this enum instance is a black piece
   */
  public boolean isBlack() { return this == BLACK_PIECE ; }

  /**
   * Returns true if this enum instance is an empty square
   */
  public boolean isEmpty() { return this == EMPTY ; }
}