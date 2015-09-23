ConsoleChess

To use:

1. Compile and run run.Main.  If you can run bash scripts and have make installed, just run "make" to build.
    Then "./run.sh" to start.
2. Move pieces (currently both sides are human controlled) by typing in the square to move from and to
	in algebraic notation (e.g. to move white's king's pawn, type "e2 e4" ["e2e4" also works]).
	Pieces on the board are denoted by a smaller case character to denote color ("w" for white, "b" for black)
	and an upper case character to denote piece type ( P - pawn, R - rook, N - knight, B - bishop, Q - queen, K - king).
3. Useful commands:
	valid "square" - list positions on the board that the piece on "square" can move to
					 (replace "square" with algebraic notation of piece's position).
	quit or exit - exit the game.
    show    - show the board

