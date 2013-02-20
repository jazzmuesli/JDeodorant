package gr.uom.java.ast.decomposition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.WhileStatement;

public class ASTNodeMatcher extends ASTMatcher{

	private List<ASTNodeDifference> differences = new ArrayList<ASTNodeDifference>();

	public List<ASTNodeDifference> getDifferences() {
		return differences;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(ASTNodeDifference diff : differences) {
			sb.append(diff.toString());
		}
		return sb.toString();
	}

	public boolean isParameterizable() {
		for(ASTNodeDifference diff : differences) {
			if(!diff.isParameterizable())
				return false;
		}
		return true;
	}

	public boolean isTypeHolder(Object o) {
		if(o.getClass().equals(MethodInvocation.class) || o.getClass().equals(SuperMethodInvocation.class)			
				|| o.getClass().equals(NumberLiteral.class) || o.getClass().equals(StringLiteral.class)
				|| o.getClass().equals(CharacterLiteral.class) || o.getClass().equals(BooleanLiteral.class)
				|| o.getClass().equals(TypeLiteral.class) 
				|| o.getClass().equals(ArrayCreation.class)
				|| o.getClass().equals(ClassInstanceCreation.class)
				|| o.getClass().equals(ArrayAccess.class) || o.getClass().equals(FieldAccess.class) || o.getClass().equals(SuperFieldAccess.class)
				|| o.getClass().equals(SimpleName.class) || o.getClass().equals(QualifiedName.class))
			return true;
		return false;
	}

	public ITypeBinding getTypeBinding(Object o) {
		if(o.getClass().equals(MethodInvocation.class)) {
			MethodInvocation methodInvocation = (MethodInvocation) o;
			return methodInvocation.resolveMethodBinding().getReturnType();
		}
		else if(o.getClass().equals(SuperMethodInvocation.class)) {
			SuperMethodInvocation superMethodInvocation = (SuperMethodInvocation) o;
			return superMethodInvocation.resolveMethodBinding().getReturnType();
		}
		else if(o.getClass().equals(NumberLiteral.class)) {
			NumberLiteral numberLiteral = (NumberLiteral) o;
			return numberLiteral.resolveTypeBinding();
		}
		else if(o.getClass().equals(StringLiteral.class)) {
			StringLiteral stringLiteral = (StringLiteral) o;
			return stringLiteral.resolveTypeBinding();
		}
		else if(o.getClass().equals(CharacterLiteral.class)) {
			CharacterLiteral characterLiteral = (CharacterLiteral) o;
			return characterLiteral.resolveTypeBinding();
		}
		else if(o.getClass().equals(BooleanLiteral.class)) {
			BooleanLiteral booleanLiteral = (BooleanLiteral) o;
			return booleanLiteral.resolveTypeBinding();
		}
		else if(o.getClass().equals(TypeLiteral.class)) {
			TypeLiteral typeLiteral = (TypeLiteral) o;
			return typeLiteral.resolveTypeBinding();
		}
		else if(o.getClass().equals(ArrayCreation.class)) {
			ArrayCreation arrayCreation = (ArrayCreation) o;
			return arrayCreation.resolveTypeBinding();
		}
		else if(o.getClass().equals(ClassInstanceCreation.class)) {
			ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) o;
			return classInstanceCreation.resolveTypeBinding();
		}
		else if(o.getClass().equals(ArrayAccess.class)) {
			ArrayAccess arrayAccess = (ArrayAccess) o;
			return arrayAccess.resolveTypeBinding();
		}
		else if(o.getClass().equals(FieldAccess.class)) {
			FieldAccess fieldAccess = (FieldAccess) o;
			return fieldAccess.resolveTypeBinding();
		}
		else if(o.getClass().equals(SuperFieldAccess.class)) {
			SuperFieldAccess superFieldAccess = (SuperFieldAccess) o;
			return superFieldAccess.resolveTypeBinding();
		}
		else if(o.getClass().equals(SimpleName.class)) {
			SimpleName simpleName = (SimpleName) o;
			return simpleName.resolveTypeBinding();
		}
		else if(o.getClass().equals(QualifiedName.class)) {
			QualifiedName qualifiedName = (QualifiedName) o;
			return qualifiedName.resolveTypeBinding();
		}
		return null;
	}

	public boolean match(ArrayAccess node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (!(other instanceof ArrayAccess)) {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			else {
				ArrayAccess o = (ArrayAccess) other;
				safeSubtreeMatch(node.getArray(), o.getArray());
				safeSubtreeMatch(node.getIndex(), o.getIndex());
			}
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(ArrayCreation node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (!(other instanceof ArrayCreation)) {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			else {
				ArrayCreation o = (ArrayCreation) other;
				if(node.dimensions().size() != o.dimensions().size())
				{
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.ARRAY_DIMENSION_MISMATCH);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
				safeSubtreeMatch(node.getType(), o.getType());
				safeSubtreeListMatch(node.dimensions(), o.dimensions());
				safeSubtreeMatch(node.getInitializer(), o.getInitializer());
			}	
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(Block node, Object other) {
		if (!(other instanceof Block)) {
			return false;
		}
		return true;
	}

	public boolean match(BooleanLiteral node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (!(other instanceof BooleanLiteral)) {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			else 
			{
				BooleanLiteral o = (BooleanLiteral) other;
				if(node.booleanValue() != o.booleanValue())
				{
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.LITERAL_VALUE_MISMATCH);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(CharacterLiteral node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (!(other instanceof CharacterLiteral)) {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			else 
			{
				CharacterLiteral o = (CharacterLiteral) other;
				if(!node.getEscapedValue().equals(o.getEscapedValue()))
				{
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.LITERAL_VALUE_MISMATCH);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(ClassInstanceCreation node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (!(other instanceof ClassInstanceCreation)) {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			else {
				ClassInstanceCreation o = (ClassInstanceCreation) other;
				if(node.arguments().size() != o.arguments().size()) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.ARGUMENT_NUMBER_MISMATCH);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
				safeSubtreeMatch(node.getAnonymousClassDeclaration(),o.getAnonymousClassDeclaration());
				safeSubtreeListMatch(node.arguments(), o.arguments());
				safeSubtreeMatch(node.getExpression(), o.getExpression());
			}
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(DoStatement node, Object other) {
		if (!(other instanceof DoStatement)) {
			return false;
		}
		DoStatement o = (DoStatement) other;
		return (
				safeSubtreeMatch(node.getExpression(), o.getExpression()));
	}

	public boolean match(EnhancedForStatement node, Object other) {
		if (!(other instanceof EnhancedForStatement)) {
			return false;
		}
		EnhancedForStatement o = (EnhancedForStatement) other;
		boolean paramMatch = safeSubtreeMatch(node.getParameter(), o.getParameter());
		boolean expMatch = safeSubtreeMatch(node.getExpression(), o.getExpression());
		return paramMatch && expMatch;
	}

	public boolean match(FieldAccess node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (other instanceof FieldAccess) {
				FieldAccess o = (FieldAccess) other;
				if(!node.getName().toString().equals(o.getName().toString())) {
					Difference diff = new Difference(node.getName().toString(),o.getName().toString(),DifferenceType.VARIABLE_NAME_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
				if(!typeMatch) {
					Difference diff = new Difference(node.resolveTypeBinding().getQualifiedName(),o.resolveTypeBinding().getQualifiedName(),DifferenceType.VARIABLE_TYPE_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
				if(!astNodeDifference.isEmpty())
					differences.add(astNodeDifference);
				return (
						safeSubtreeMatch(node.getExpression(), o.getExpression()));
			}
			else {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
				}
				else {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
			}
			if(!astNodeDifference.isEmpty())
				differences.add(astNodeDifference);
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(ForStatement node, Object other) {
		if (!(other instanceof ForStatement)) {
			return false;
		}
		ForStatement o = (ForStatement) other;
		boolean initializerMatch = safeSubtreeListMatch(node.initializers(), o.initializers());
		boolean expMatch = safeSubtreeMatch(node.getExpression(), o.getExpression());
		boolean updaterMatch = safeSubtreeListMatch(node.updaters(), o.updaters());
		return initializerMatch && expMatch && updaterMatch;
	}
	
	public boolean match(IfStatement node, Object other) {
		if (!(other instanceof IfStatement)) {
			return false;
		}
		IfStatement o = (IfStatement) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression()));
	}

	public boolean match(LabeledStatement node, Object other) {
		if (!(other instanceof LabeledStatement)) {
			return false;
		}
		LabeledStatement o = (LabeledStatement) other;
		return (
				safeSubtreeMatch(node.getLabel(), o.getLabel()));
	}

	public boolean match(MethodInvocation node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveMethodBinding().getReturnType().isEqualTo(getTypeBinding(other));
			if (!(other instanceof MethodInvocation)) {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			else {
				MethodInvocation o = (MethodInvocation) other;
				if(node.arguments().size() != o.arguments().size()) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.ARGUMENT_NUMBER_MISMATCH);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
				safeSubtreeMatch(node.getName(), o.getName());
				safeSubtreeListMatch(node.arguments(), o.arguments());
				safeSubtreeMatch(node.getExpression(), o.getExpression());
			}
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(NumberLiteral node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (!(other instanceof NumberLiteral)) {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			else 
			{
				NumberLiteral o = (NumberLiteral) other;
				if(!node.getToken().equals(o.getToken()))
				{
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.LITERAL_VALUE_MISMATCH);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(QualifiedName node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (other instanceof QualifiedName) {
				QualifiedName o = (QualifiedName) other;
				if(!node.getName().toString().equals(o.getName().toString())) {
					Difference diff = new Difference(node.getName().toString(),o.getName().toString(),DifferenceType.VARIABLE_NAME_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
				if(!typeMatch) {
					Difference diff = new Difference(node.resolveTypeBinding().getQualifiedName(),o.resolveTypeBinding().getQualifiedName(),DifferenceType.VARIABLE_TYPE_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
				if(!astNodeDifference.isEmpty()) 
					differences.add(astNodeDifference);
				return (
						safeSubtreeMatch(node.getQualifier(), o.getQualifier()));
			}
			else {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
				}
				else {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
			}
			if(!astNodeDifference.isEmpty()) 
				differences.add(astNodeDifference);
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(SimpleName node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (other instanceof SimpleName) {
				SimpleName o = (SimpleName) other;
				if(!node.getIdentifier().equals(o.getIdentifier())) {
					IBinding nodeBinding = node.resolveBinding();
					IBinding otherBinding = o.resolveBinding();
					if(nodeBinding != null && otherBinding != null && nodeBinding.getKind() == IBinding.METHOD && otherBinding.getKind() == IBinding.METHOD) {
						Difference diff = new Difference(node.getIdentifier(),o.getIdentifier(),DifferenceType.METHOD_INVOCATION_NAME_MISMATCH);
						astNodeDifference.addDifference(diff);
					}
					else {
						Difference diff = new Difference(node.getIdentifier(),o.getIdentifier(),DifferenceType.VARIABLE_NAME_MISMATCH);
						astNodeDifference.addDifference(diff);
					}
				}
				if(!typeMatch) {
					Difference diff = new Difference(node.resolveTypeBinding().getQualifiedName(),o.resolveTypeBinding().getQualifiedName(),DifferenceType.VARIABLE_TYPE_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
			}
			else {
				if(typeMatch) {
					Difference diff = new Difference(node.getIdentifier(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
				}
				else {
					Difference diff = new Difference(node.getIdentifier(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
			}
			if(!astNodeDifference.isEmpty()) 
				differences.add(astNodeDifference);
			return typeMatch;
		}
		Difference diff = new Difference(node.getIdentifier(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(StringLiteral node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (!(other instanceof StringLiteral)) {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			else 
			{
				StringLiteral o = (StringLiteral) other;
				if(!node.getLiteralValue().equals(o.getLiteralValue()))
				{
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.LITERAL_VALUE_MISMATCH);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(SuperFieldAccess node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveTypeBinding().isEqualTo(getTypeBinding(other));
			if (other instanceof SuperFieldAccess) {
				SuperFieldAccess o = (SuperFieldAccess) other;
				if(!node.getName().toString().equals(o.getName().toString())) {
					Difference diff = new Difference(node.getName().toString(),o.getName().toString(),DifferenceType.VARIABLE_NAME_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
				if(!typeMatch) {
					Difference diff = new Difference(node.resolveTypeBinding().getQualifiedName(),o.resolveTypeBinding().getQualifiedName(),DifferenceType.VARIABLE_TYPE_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
			}
			else {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
				}
				else {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
					astNodeDifference.addDifference(diff);
				}
			}
			if(!astNodeDifference.isEmpty()) 
				differences.add(astNodeDifference);
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(SuperMethodInvocation node, Object other) {
		AbstractExpression exp1 = new AbstractExpression(node);
		AbstractExpression exp2 = new AbstractExpression((Expression)other);
		ASTNodeDifference astNodeDifference = new ASTNodeDifference(exp1, exp2);
		if(isTypeHolder(other)) {
			boolean typeMatch = node.resolveMethodBinding().getReturnType().isEqualTo(getTypeBinding(other));
			if (!(other instanceof SuperMethodInvocation)) {
				if(typeMatch) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.TYPE_COMPATIBLE_REPLACEMENT);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
			}
			else {
				SuperMethodInvocation o = (SuperMethodInvocation) other;
				if(node.arguments().size() != o.arguments().size()) {
					Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.ARGUMENT_NUMBER_MISMATCH);
					astNodeDifference.addDifference(diff);
					differences.add(astNodeDifference);
				}
				safeSubtreeMatch(node.getName(), o.getName());
				safeSubtreeListMatch(node.arguments(), o.arguments());
				safeSubtreeMatch(node.getQualifier(), o.getQualifier());
			}
			return typeMatch;
		}
		Difference diff = new Difference(node.toString(),other.toString(),DifferenceType.AST_TYPE_MISMATCH);
		astNodeDifference.addDifference(diff);
		differences.add(astNodeDifference);
		return false;
	}

	public boolean match(SwitchStatement node, Object other) {
		if (!(other instanceof SwitchStatement)) {
			return false;
		}
		SwitchStatement o = (SwitchStatement) other;
		return (
				safeSubtreeMatch(node.getExpression(), o.getExpression()));
	}

	public boolean match(SynchronizedStatement node, Object other) {
		if (!(other instanceof SynchronizedStatement)) {
			return false;
		}
		SynchronizedStatement o = (SynchronizedStatement) other;
		return (
				safeSubtreeMatch(node.getExpression(), o.getExpression()));
	}

	public boolean match(TryStatement node, Object other) {
		if (!(other instanceof TryStatement)) {
			return false;
		}
		TryStatement o = (TryStatement) other;
		boolean resourceMatch = safeSubtreeListMatch(node.resources(), o.resources());
		boolean catchClauseMatch = safeSubtreeListMatch(node.catchClauses(), o.catchClauses());
		boolean finallyClauseMatch =  safeSubtreeMatch(node.getFinally(), o.getFinally());
		return resourceMatch && catchClauseMatch && finallyClauseMatch;
	}

	public boolean match(TypeLiteral node, Object other) {
		if(isTypeHolder(other)) {
			return (node.resolveTypeBinding().isEqualTo(getTypeBinding(other)));
		}
		return false;
	}

	public boolean match(WhileStatement node, Object other) {
		if (!(other instanceof WhileStatement)) {
			return false;
		}
		WhileStatement o = (WhileStatement) other;
		return (
				safeSubtreeMatch(node.getExpression(), o.getExpression()));
	}
}