package cellsociety.simulation.cell;

public class ChouReggiaLoopCell extends RuleTableCell{

  public static final String RULE_TABLE = "000000 000440 000547 000100 000110 000330 004040 004445 "
      + "004100 001040 001010 001740 003000 003010 003030 007040 007030 007104 007110 040000 040070 "
      + "047100 050007 017000 070000 070077 071010 400101 400313 401033 407103 411033 431033 500033 "
      + "503330 100045 100011 100414 101044 101301 103011 107131 140414 141044 111044 133011 177711 "
      + "304011 305011 305141 307141 344011 345011 354010 314001 377711 700000 700337 707100 707141 "
      + "707110 717000 730007 770070 770711";

  public ChouReggiaLoopCell() {
    super();
    ruleTable = RULE_TABLE;
    ruleMap = getRuleTableMap(RULE_TABLE);
  }

  /*@Override
  protected String getSurrounds(Cell[] neighbors) {
    StringBuilder surround = new StringBuilder();
    for (int i = neighbors.length-1; i >= 0; i -= 2) {
      surround.append(neighbors[i]);
    }
    return surround.toString();
  }*/

}
