package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import jakarta.annotation.Nonnull;

import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import com.google.common.collect.ImmutableSet;
import java.util.*;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;
/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	private final class MyGameState implements GameState {
		private final GameSetup setup;
		private final ImmutableSet<Piece> remaining;
		private final ImmutableList<LogEntry> log;
		private final Player mrX;
		private final List<Player> detectives;
		private ImmutableSet<Move> moves;
		private ImmutableSet<Piece> winner;


		private MyGameState(
						final GameSetup setup,
						final ImmutableSet<Piece> remaining,
						final ImmutableList<LogEntry> log,
						final Player mrX,
						final ImmutableList<Player> detectives) {

			this.setup = setup;
			this.remaining = remaining;
			this.log = log;
			this.mrX = mrX;
			this.detectives = detectives;
		}

		@Override
		public GameState advance(Move move) {
//			return move.accept(new Move.Visitor<GameState>() {
//				@Override
//				public GameState visit(Move.SingleMove move) {
////					Mr X
//					move.commencedBy().isMrX();
////					Detectives
//					return null;
//				}
//			}
			return null;
		}

		@Override
		public GameSetup getSetup() {  return setup; }

		@Override
		public ImmutableSet<Piece> getPlayers() {
			Set<Piece> players = new HashSet<>();
			players.add(mrX.piece());
			for (Player player : detectives) {
				players.add(player.piece());
			}
			return ImmutableSet.copyOf(players);
		}

		@Override
		public Optional<Integer> getDetectiveLocation(Detective detective)  {
			for (Player player : detectives) {
				if (player.piece() == detective) {
					return Optional.of(player.location());
				}
			}
			return Optional.empty();
		}

		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
//			Mr X
			if (mrX.piece() == piece) {
				return Optional.of(ticket -> mrX.tickets().get(ticket));
			}


//			Detectives
			for (Player player : detectives) {
				if (player.piece() == piece) {
					return Optional.of(ticket -> player.tickets().get(ticket));
				}
			}
			return Optional.empty();
		}

		@Override
		public ImmutableList<LogEntry> getMrXTravelLog()  {  return log; }

		@Override
		public ImmutableSet<Piece> getWinner()  {
			return ImmutableSet.of();
		}

		@Override
		public ImmutableSet<Move> getAvailableMoves()   {
			Set<Move> moves = new HashSet<>();
			moves.addAll(makeSingleMoves(setup, detectives, mrX, mrX.location()));
			moves.addAll(makeDoubleMoves(setup, detectives, mrX, mrX.location(), log));

			return ImmutableSet.copyOf(moves);
		}

//		Helper function for getAvailableMoves()
		private static Set<SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source){
			Set<SingleMove> singleMoves = new HashSet<>();

			for(int destination : setup.graph.adjacentNodes(source)) {
//				Check if destination is occupied by a detective
				boolean occupied = false;
				for (Player d : detectives) {
					if (d.location() == destination) {
						occupied = true;
						break;
					}
				}
				if (occupied) continue;

				for(Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of()) ) {
					// Check if the player has the required tickets
					Ticket ticket = t.requiredTicket();
					if (player.has(ticket)) {
						singleMoves.add(new SingleMove(player.piece(), source, ticket, destination));
					}

//					Check if the player is MrX and has secret ticket
					if (player.isMrX() && player.has(Ticket.SECRET)) {
						singleMoves.add(new SingleMove(player.piece(), source, Ticket.SECRET, destination));
					}
				}

			}
			return singleMoves;
		}

//		Helper function for getAvailableMoves()
		private static Set<DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player player, int source, ImmutableList<LogEntry> log){
			Set<DoubleMove> doubleMoves = new HashSet<>();
			boolean atLeastTwoRoundsLeft = (log.size() + 1) < setup.moves.size();

//			Check if it is MrX, has secret ticket and has at least 2 rounds left
			if (!player.isMrX() || !player.has(Ticket.DOUBLE) || !atLeastTwoRoundsLeft) { return doubleMoves; }


//			DoubleMove is form by 2 single moves
//			So it first use 1 ticket to do the first move
//			After that do the second move
//			Finally add to doubleMoves set
			for (SingleMove firstMove : makeSingleMoves(setup, detectives, player, source)) {
				Player afterFirstMove = player.use(firstMove.ticket).at(firstMove.destination);

				for (SingleMove secondMove : makeSingleMoves(setup, detectives, afterFirstMove, firstMove.destination)) {
					doubleMoves.add(new DoubleMove(player.piece(), source, firstMove.ticket, firstMove.destination, secondMove.ticket, secondMove.destination));
				}
			}

			return doubleMoves;
		}
	}

	@Nonnull
	@Override
	public GameState build(GameSetup setup, Player mrX, ImmutableList<Player> detectives) {

//		Prevent empty moves
		if(setup.moves.isEmpty()) throw new IllegalArgumentException();

		//		Prevent empty graph
		if (setup.graph.nodes().isEmpty()) throw new IllegalArgumentException();

//		Prevent mrX and detectives are null
		if (mrX == null || detectives == null) throw new NullPointerException();

//		Prevent no MrX
		if (mrX.piece() != Piece.MrX.MRX) throw new IllegalArgumentException();

		Set<Piece> seenDetectives = new HashSet<>();
		Set<Integer> seenLocations = new HashSet<>();
		for (Player player : detectives) {
//			Prevent more than 1 MrX
			if (player.isMrX())  throw new IllegalArgumentException();
//			Prevent duplicate detectives
			if (!seenDetectives.add(player.piece()))  throw new IllegalArgumentException();
//			Prevent location overlap between detectives
			if (!seenLocations.add(player.location()))  throw new IllegalArgumentException();
//			Prevent detectives have SECRET and DOUBLE ticket
			if (player.has(Ticket.SECRET) || player.has(Ticket.DOUBLE))  throw new IllegalArgumentException();
		}

		return new MyGameState(setup, ImmutableSet.of(MrX.MRX), ImmutableList.of(), mrX, detectives);
	}
}
