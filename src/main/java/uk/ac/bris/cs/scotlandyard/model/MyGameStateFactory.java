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
		public GameState advance(Move move) {return null;}

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
		public ImmutableSet<Move> getAvailableMoves()   {  return null; }
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
