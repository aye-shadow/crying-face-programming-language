package Symbols;

import java.util.ArrayList;
import java.util.List;

abstract public class Symbol {
    protected String id_name;   // Identifier name
    protected String type;      // Type (e.g., int, float, etc.)
    protected String scope;     // Scope (e.g., global, local, func, block)

    public Symbol(String id_name, String type, String scope) {
        this.id_name = id_name;
        this.type = type;
        this.scope = scope;
    }

    public String getName() {
        return id_name;
    }

    public String getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public abstract String toString(); // Abstract method for string representation
}

// Inherited class for variable symbols
class var_symbol extends Symbol
{
    public String value;
    public var_symbol(String id_name, String type, String scope)
    {
        super(id_name, type, scope);
    }

    @Override
    public String toString()
    {
        return String.format("variable[id_name=%s, type=%s, scope=%s, value=%s]", id_name, type, scope, value);
    }
}

// Inherited class for function symbols
class func_symbol extends Symbol
{
    private List<var_symbol> parameters; // List of parameters

    public func_symbol(String name, String returnType) {
        super(name, returnType, "global");
        this.parameters = new ArrayList<>();
    }

    public void addParameter(String name, String type) {
        parameters.add(new var_symbol(name, type, this.id_name));
    }

    public List<var_symbol> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return String.format("function[name=%s, returnType=%s, scope=%s, parameters=%s]",
                id_name, type, parameters);
    }
}