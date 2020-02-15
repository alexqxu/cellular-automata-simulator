package cellsociety.simulation.cell;

/**
 * @author Maverick Chung, mc608
 *
 * Purpose: Represents a self-replicating machine with a rule table developed by Byl. Reads the rules
 * from the rule table listed below.
 *
 * Assumptions: See Cell
 *
 * Dependencies: Cell
 */
public class BylLoopCell extends RuleTableCell {

  public static final String RULE_TABLE = "000000 000010 000031 000420 000110 000120 000311 000330 "
      + "000320 000200 000242 000233 004330 005120 001400 001120 001305 001310 001320 003020 003400"
      + " 003420 003120 002040 002050 002010 002020 002420 002500 002520 002200 002250 002220 040000"
      + " 050000 050500 010052 010022 010400 010100 020055 400233 405235 401233 442024 452020 415233"
      + " 411233 412024 412533 432024 421433 422313 501302 502230 540022 542002 512024 530025 520025"
      + " 520442 523242 522020 100000 100010 100033 100330 101233 103401 103244 111244 113244 112404"
      + " 133244 121351 123444 123543 122414 122434 300100 300300 300211 300233 304233 301100 301211"
      + " 303211 303233 302233 344233 343233 351202 353215 314233 311233 313251 313211 312211 335223"
      + " 333211 320533 324433 325415 321433 321511 321321 323411 200000 200042 200032 200022 200442"
      + " 200515 200112 200122 200342 200332 200242 200212 201502 203202 202302 244022 245022 242042"
      + " 242022 254202 255042 252025 210042 214022 215022 212055 212022 230052 234022 235002 235022"
      + " 232042 232022 220042 220020 220533 221552";

  public BylLoopCell() {
    super();
    ruleTable = RULE_TABLE;
    ruleMap = getRuleTableMap(RULE_TABLE);
  }

}
