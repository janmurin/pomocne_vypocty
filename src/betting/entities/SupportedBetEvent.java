package betting.entities;

import java.awt.*;

/**
 * Created by janmu on 13.08.2016.
 */
public class SupportedBetEvent extends CleanBetEvent {

    public Sport sport;
    public League league;
    public EventType eventType;
    public Competitor homeCompetitor;
    public Competitor awayCompetitor;
    public CleanBetEvent cbe;

    /**
     *
     * @param sport
     * @param league
     * @param eventType
     * @param homeCompetitor
     * @param awayCompetitor
     * @param cbe
     */
    public SupportedBetEvent(Sport sport, League league, EventType eventType, Competitor homeCompetitor, Competitor awayCompetitor, CleanBetEvent cbe) {
        this.sport = sport;
        this.league = league;
        this.eventType = eventType;
        this.homeCompetitor = homeCompetitor;
        this.awayCompetitor = awayCompetitor;
        this.cbe = cbe;
    }
}
