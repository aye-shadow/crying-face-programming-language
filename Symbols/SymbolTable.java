package Symbols;

import Scanner.Scanner;
import Scanner.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable
{
    private static SymbolTable instance; // Singleton instance
    private Map<String, Symbol> table;

    public SymbolTable()
    {
        table = new HashMap<>();
    }
    public static SymbolTable getInstance()
    {
        if (instance == null)
        {
            instance = new SymbolTable();
        }
        return instance;
    }

    //
    public void addSymbol(String id_name, String type, String scope)
    {
        if (table.containsKey(id_name))
                 return; // do this asw [ error |
        Symbol symbol = new var_symbol(id_name, type, scope);
        table.put(id_name, symbol);
    }
    // have to set its TYPE
    public void update_value(String id_name, String value)
    {
        var_symbol s = (var_symbol) table.get(id_name);
        //
        s.value = value;
    }


    public Symbol getSymbol(String id_name)
    {
        return table.get(id_name);
    }


    public boolean contains(String id_name)
    {
        return table.containsKey(id_name);
    }

    public void display()
    {
        System.out.println("Symbol Table:");
        for (Symbol symbol : table.values()) {
            System.out.println(symbol);
        }
    }

    public static void main(String[] args)
    {
        Scanner tokenizer = new Scanner("D:\\projects\\New folder\\States\\transition_table.txt");
      //  tokenizer.printTransitionTable();

        String inputFilePath = "D:\\projects\\New folder\\InputFiles\\test1.\uD83D\uDE2D";
        String input = tokenizer.readInputFromFile(inputFilePath);
        // System.out.println(input);
        Token[] tokens = tokenizer.getTokens(input);


    }
}