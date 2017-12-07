import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("Duplicates") // Remove IntelliJ warning about duplicate code

public class SecondVisitor extends GJDepthFirst<String, SymbolTable> {

    private String currentClassName;
    private String currentFunctionName;
    private Boolean classVar;
    private Boolean functionVar;
    private String exprType;
    private Boolean returnPrimaryExpr;
    private ArrayList<String> methodArgs;

    public String look_up_identifier(String identifier, SymbolTable symbolTable) throws Exception {
        // Lookup if this identifier is declared before
        SymbolTable.ClassSymTable curClass = symbolTable.classes.get(this.currentClassName);
        SymbolTable.MethodSymTable curMethod = curClass.methods.get(this.currentFunctionName);
        // If you find it one of the below cases, return its type
        // Check if this identifier is a parameter or a variable
        if (curMethod.parameters.containsKey(identifier)) {
            return curMethod.parameters.get(identifier);
        }
        if (curMethod.variables.containsKey(identifier)) {
            return curMethod.variables.get(identifier);
        }
        // Check if it is field in the class
        if (curClass.fields.containsKey(identifier)) {
            return curClass.fields.get(identifier);
        }
        // Check if it has parent class with this field
        while (curClass.parentClassName != null) {
            SymbolTable.ClassSymTable parentClass = symbolTable.classes.get(curClass.parentClassName);
            if (parentClass.fields.containsKey(identifier)) {
                return parentClass.fields.get(identifier);
            }
            curClass = parentClass;
        }
        // If you are here then this identifier was not found...
        return null;
//        throw new Exception("Unknown symbol '" + identifier + "'");
    }

    public String[] look_up_methods(String methodName, String className, SymbolTable symbolTable) throws Exception {
        SymbolTable.ClassSymTable classSym = symbolTable.classes.get(className);
        if (classSym.methods.containsKey(methodName)) {
            // Return your type and class name
            return new String[] {classSym.methods.get(methodName).returnType, classSym.className};

        }
        // Check if this method is in parent class
        while (classSym.parentClassName != null) {
            SymbolTable.ClassSymTable parentClass = symbolTable.classes.get(classSym.parentClassName);
            if (parentClass.methods.containsKey(methodName)) {
                // Return your type and class name
                return new String[] {parentClass.methods.get(methodName).returnType, parentClass.className};
            }
            classSym = parentClass;
        }
        return null;
//        throw new Exception("Unknown method '" + methodName + "'");
    }

    public void check_args_method(String methodName, String className, ArrayList args, SymbolTable symbolTable) throws Exception {
        SymbolTable.ClassSymTable classSym = symbolTable.classes.get(className);
        SymbolTable.MethodSymTable methodSymTable = classSym.methods.get(methodName);
        // If the args array is empty
        if(args.isEmpty()){
            if(!methodSymTable.parameters.isEmpty()){
                throw new Exception("The number of the given arguments is not matching with the definition of the method '" + methodName + "'");
            }
        }
        else {
            if (methodSymTable.parameters.size() != args.size()) {
                throw new Exception("The number of the given arguments is not matching with the definition of the method '" + methodName + "'");
            }
            for (Map.Entry methodEntryFunctions : methodSymTable.parameters.entrySet()) {
                String paramType = methodEntryFunctions.getValue().toString();
//            if () {
//                throw new Exception("Type of '"+  +"");
//            }
            }
        }

    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public String visit(MainClass n, SymbolTable symbolTable) throws Exception {
        this.currentClassName = n.f1.accept(this, symbolTable);
        this.currentFunctionName = "main";
        this.classVar = false;
        this.functionVar = true;
        // Visit Statement
        n.f15.accept(this, symbolTable);
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public String visit(ClassDeclaration n, SymbolTable symbolTable) throws Exception {
        this.currentClassName = n.f1.accept(this, symbolTable);
        this.classVar = false;
        this.functionVar = true;
        // Visit MethodDeclaration
        n.f4.accept(this, symbolTable);
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public String visit(ClassExtendsDeclaration n, SymbolTable symbolTable) throws Exception {
        this.currentClassName = n.f1.accept(this, symbolTable);
        this.classVar = false;
        this.functionVar = true;
        // Visit MethodDeclaration
        n.f6.accept(this, symbolTable);
        return null;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public String visit(MethodDeclaration n, SymbolTable symbolTable) throws Exception {
        this.currentFunctionName = n.f2.accept(this, symbolTable);
        this.classVar = false;
        this.functionVar = true;
        // Visit Statement
        n.f8.accept(this, symbolTable);
        return null;
    }

    /**
     * f0 -> Block()
     * | AssignmentStatement()
     * | ArrayAssignmentStatement()
     * | IfStatement()
     * | WhileStatement()
     * | PrintStatement()
     */
    public String visit(Statement n, SymbolTable symbolTable) throws Exception {
        return n.f0.accept(this, symbolTable);
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public String visit(Block n, SymbolTable symbolTable) throws Exception {
        String _ret = null;
        n.f0.accept(this, symbolTable);
        n.f1.accept(this, symbolTable);
        n.f2.accept(this, symbolTable);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n, SymbolTable symbolTable) throws Exception {
        String identifier = n.f0.accept(this, symbolTable);
        String type = look_up_identifier(identifier, symbolTable);
        if (type == null) {
            throw new Exception("Unknown symbol '" + identifier + "'");
        }
        this.exprType = type;
//        this.assignmentStatement = true;
        String exrpType = n.f2.accept(this, symbolTable);

        if (!this.exprType.equals(exrpType)) {
            throw new Exception("Operations between '" + this.exprType + "' and '" + exrpType + "' are not permitted");
        }
//        this.assignmentStatement = false;
        this.exprType = null;
        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public String visit(ArrayAssignmentStatement n, SymbolTable symbolTable) throws Exception {
        String _ret = null;
        n.f0.accept(this, symbolTable);
        n.f1.accept(this, symbolTable);
        n.f2.accept(this, symbolTable);
        n.f3.accept(this, symbolTable);
        n.f4.accept(this, symbolTable);
        n.f5.accept(this, symbolTable);
        n.f6.accept(this, symbolTable);
        return _ret;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public String visit(IfStatement n, SymbolTable symbolTable) throws Exception {
        String _ret = null;
        n.f0.accept(this, symbolTable);
        n.f1.accept(this, symbolTable);
        n.f2.accept(this, symbolTable);
        n.f3.accept(this, symbolTable);
        n.f4.accept(this, symbolTable);
        n.f5.accept(this, symbolTable);
        n.f6.accept(this, symbolTable);
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement n, SymbolTable symbolTable) throws Exception {
        String _ret = null;
        n.f0.accept(this, symbolTable);
        n.f1.accept(this, symbolTable);
        n.f2.accept(this, symbolTable);
        n.f3.accept(this, symbolTable);
        n.f4.accept(this, symbolTable);
        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement n, SymbolTable symbolTable) throws Exception {
        this.returnPrimaryExpr = true;
        this.exprType = "int";
        String type = n.f2.accept(this, symbolTable);
        if (!type.equals("int")) {
            throw new Exception("Only integers allowed to be printed");
        }
        this.returnPrimaryExpr = false;
        this.exprType = null;
        return null;
    }

    /**
     * f0 -> AndExpression()
     * | CompareExpression()
     * | PlusExpression()
     * | MinusExpression()
     * | TimesExpression()
     * | ArrayLookup()
     * | ArrayLength()
     * | MessageSend()
     * | Clause()
     */
    public String visit(Expression n, SymbolTable symbolTable) throws Exception {
        return n.f0.accept(this, symbolTable);
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    public String visit(AndExpression n, SymbolTable symbolTable) throws Exception {
        String _ret = null;
        n.f0.accept(this, symbolTable);
        n.f1.accept(this, symbolTable);
        n.f2.accept(this, symbolTable);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression n, SymbolTable symbolTable) throws Exception {
        String _ret = null;
        n.f0.accept(this, symbolTable);
        n.f1.accept(this, symbolTable);
        n.f2.accept(this, symbolTable);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public String visit(PlusExpression n, SymbolTable symbolTable) throws Exception {
        this.returnPrimaryExpr = false;
        String type1 = n.f0.accept(this, symbolTable);
        this.returnPrimaryExpr = false;
        String type2 = n.f2.accept(this, symbolTable);
        if (!type1.equals("int") || !type2.equals("int")) {
            throw new Exception("'+' operator works only for integers");
        }
        this.returnPrimaryExpr = true;
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public String visit(MinusExpression n, SymbolTable symbolTable) throws Exception {
        this.returnPrimaryExpr = false;
        String type1 = n.f0.accept(this, symbolTable);
        this.returnPrimaryExpr = false;
        String type2 = n.f2.accept(this, symbolTable);
        if (!type1.equals("int") || !type2.equals("int")) {
            throw new Exception("'-' operator works only for integers");
        }
        this.returnPrimaryExpr = false;
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public String visit(TimesExpression n, SymbolTable symbolTable) throws Exception {
        this.returnPrimaryExpr = false;
        String type1 = n.f0.accept(this, symbolTable);
        this.returnPrimaryExpr = false;
        String type2 = n.f2.accept(this, symbolTable);
        if (!type1.equals("int") || !type2.equals("int")) {
            throw new Exception("'*' operator works only for integers");
        }
        this.returnPrimaryExpr = false;
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup n, SymbolTable symbolTable) throws Exception {
        String type1 = n.f0.accept(this, symbolTable);
        if (!type1.equals("int[]")) {
            throw new Exception("This is not type of int[]");
        }
        String type2 = n.f2.accept(this, symbolTable);
        if (!type2.equals("int")) {
            throw new Exception("int arrays must have type of 'int' iterators");
        }
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public String visit(ArrayLength n, SymbolTable symbolTable) throws Exception {
        String _ret = null;
        n.f0.accept(this, symbolTable);
        n.f1.accept(this, symbolTable);
        n.f2.accept(this, symbolTable);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend n, SymbolTable symbolTable) throws Exception {
        this.returnPrimaryExpr = true;
        // Visit PrimaryExpression
        String var = n.f0.accept(this, symbolTable);
        String varType = look_up_identifier(var, symbolTable);
        // The only case that is allowed without object name
        // Is when the object is created in the same line
        if (varType == null) {
            if (!symbolTable.classes.containsKey(var)) {
                throw new Exception("Unknown symbol '" + var + "'");
            } else {
                varType = var;
            }

        }

        String methodName = n.f2.accept(this, symbolTable);
        // Lookup if method exists
        String[] retValues = look_up_methods(methodName, varType, symbolTable);
        String methodType = retValues[0];
        String className = retValues[1];

        // Create an array to hold up the types of parameters
        // If it is already created then it is assumed where are in nested call
        // Hold the data of the current array and create a new one to typecheck the nested call
        ArrayList<String> backupMethodArgs = null;
        boolean methodArgsTempFlag = false;
        if(this.methodArgs!=null) {
            methodArgsTempFlag = true;
            backupMethodArgs = new ArrayList<>(this.methodArgs);
        }
        this.methodArgs = new ArrayList<String>();

        // Visit ExpressionList
        n.f4.accept(this, symbolTable);
        // Lookup arg types with the parameter types that are defined in the method that is called
        check_args_method(methodName, className, this.methodArgs, symbolTable);

        // Erase the array
        this.methodArgs = null;
        // Restore previous array if was existed before the MessageSend visit
        if(methodArgsTempFlag) {
            this.methodArgs = new ArrayList<>(backupMethodArgs);
        }
        this.returnPrimaryExpr = false;
        return methodType;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList n, SymbolTable symbolTable) throws Exception {
        this.returnPrimaryExpr = false;
        String var = n.f0.accept(this, symbolTable);
        System.out.println(var);
        this.methodArgs.add(var);
        n.f1.accept(this, symbolTable);
        return null;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public String visit(ExpressionTail n, SymbolTable symbolTable) throws Exception {
        return n.f0.accept(this, symbolTable);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public String visit(ExpressionTerm n, SymbolTable symbolTable) throws Exception {
        this.methodArgs.add(n.f1.accept(this, symbolTable));
        return null;
    }

    /**
     * f0 -> NotExpression()
     * | PrimaryExpression()
     */
    public String visit(Clause n, SymbolTable symbolTable) throws Exception {
        return n.f0.accept(this, symbolTable);
    }

    /**
     * f0 -> IntegerLiteral()
     * | TrueLiteral()
     * | FalseLiteral()
     * | Identifier()
     * | ThisExpression()
     * | ArrayAllocationExpression()
     * | AllocationExpression()
     * | BracketExpression()
     */
    public String visit(PrimaryExpression n, SymbolTable symbolTable) throws Exception {
        String expression = n.f0.accept(this, symbolTable);
        if (expression == null) {
            return null;
        }
        if (this.returnPrimaryExpr) {
            this.returnPrimaryExpr = false;
            return expression;
        }

        // The below are for simple assignments
        // If it is integer literal
        if (expression.equals("##INT_LIT")) {
//            if (!this.exprType.equals("int")) {
//                throw new Exception("Operations between '" + this.exprType + "' and 'int' are not permitted");
//            }
            this.returnPrimaryExpr = false;
            return "int";
        } else if (expression.equals("true") || expression.equals("false")) {
//            if (!this.exprType.equals("boolean")) {
//                throw new Exception("Operations between '" + this.exprType + "' and 'boolean' are not permitted");
//            }
            this.returnPrimaryExpr = false;
            return "boolean";
        } else if (expression.equals("this")) {
            this.returnPrimaryExpr = false;
            return "this";
        } else {
            String type = look_up_identifier(expression, symbolTable);
            if (type == null) {
                throw new Exception("Unknown symbol '" + expression + "'");
            }
//            if (!this.exprType.equals(type)) {
//                throw new Exception("Operations between '" + this.exprType + "' and '" + type + "' are not permitted");
//            }
            this.returnPrimaryExpr = false;
            return type;
        }
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(ArrayAllocationExpression n, SymbolTable symbolTable) throws Exception {
        String type = n.f3.accept(this, symbolTable);
        if (!type.equals("int")) {
            throw new Exception("Array allocation size must be integer");
        }
        this.returnPrimaryExpr = true;
        return "int[]";
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression n, SymbolTable symbolTable) throws Exception {
        this.returnPrimaryExpr = true;
        String identifier = n.f1.accept(this, symbolTable);
        // Return if the type is correct
        if (!symbolTable.classes.containsKey(identifier)) {
            throw new Exception("Unkown symbol '" + identifier + "'");
        }
        SymbolTable.ClassSymTable curClass = symbolTable.classes.get(identifier);
        String type = curClass.className;
        // If the types of the assignment are not matching
        if (!exprType.equals(type)) {
            // Check if it has parent with that type
            while (curClass.parentClassName != null) {
                if (exprType.equals(curClass.parentClassName)) {
                    return curClass.parentClassName;
                }
                curClass = symbolTable.classes.get(curClass.parentClassName);
            }
        }
        return identifier;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression n, SymbolTable symbolTable) throws Exception {
        this.returnPrimaryExpr = true;
        String type = n.f1.accept(this, symbolTable);
        this.returnPrimaryExpr = true;
        return type;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, SymbolTable symbolTable) throws Exception {
        return "##INT_LIT";
    }

    /**
     * f0 -> "true"
     */
    public String visit(TrueLiteral n, SymbolTable symbolTable) throws Exception {
        return "true";
    }

    /**
     * f0 -> "false"
     */
    public String visit(FalseLiteral n, SymbolTable symbolTable) throws Exception {
        return "false";
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, SymbolTable symbolTable) throws Exception {
        return n.f0.toString();
    }

    /**
     * f0 -> "this"
     */
    public String visit(ThisExpression n, SymbolTable symbolTable) throws Exception {
        return "this";
    }

}
