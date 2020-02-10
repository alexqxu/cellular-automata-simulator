package cellsociety.simulation.cell;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public abstract class RuleTableCell extends Cell{
  protected String ruleTable;
  protected Map<Integer, HashMap<String, Integer>> ruleMap;

  public RuleTableCell(){
    super();
    defaultEdge = Cell.INFINTE;
  }

  @Override
  void planUpdate(Cell[] neighbors, Queue<Cell> cellQueue) {
    Map<String, Integer> stateRules = ruleMap.get(state);
    if (stateRules == null) {
      nextState = state;
      return;
    }
    String surround = getSurrounds(neighbors);
    nextState = stateRules.getOrDefault(surround, state);
  }

  protected String getSurrounds(Cell[] neighbors) {
    StringBuilder surround = new StringBuilder();
    for (int i = 0; i < neighbors.length; i += 2) {
      surround.append(neighbors[i]);
    }
    return surround.toString();
  }

  public Map<Integer, HashMap<String, Integer>> getRuleTableMap(String ruleTable) {
    String[] rules = ruleTable.split(" ");
    HashMap<Integer, HashMap<String, Integer>> ret = new HashMap<>();
    for (String rule : rules) {
      int st = Integer.parseInt("" + rule.charAt(0));
      int nextSt = Integer.parseInt("" + rule.charAt(rule.length() - 1));
      ret.putIfAbsent(st, new HashMap<>());
      HashMap<String, Integer> mapRule = ret.get(st);
      String[] combos = getStringRotations(rule.substring(1, rule.length() - 1));
      for (String combo : combos) {
        mapRule.put(combo, nextSt);
      }
    }
    return ret;
  }

  private String[] getStringRotations(String str) {
    String[] ret = new String[str.length()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = str;
      str = str.substring(1) + str.charAt(0);
    }
    return ret;
  }
}
