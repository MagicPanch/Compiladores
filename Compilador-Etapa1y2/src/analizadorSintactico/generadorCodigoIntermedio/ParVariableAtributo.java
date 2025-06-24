package analizadorSintactico.generadorCodigoIntermedio;

public class ParVariableAtributo {
	String variable = null; //es una variable generada por una instancia de una clase, que se le debe asignar el valor del atributo al momento de finalizar la invocacion a un metodo de la clase a la que corresponde el atributo original
	String atributo = null; //es el atributo real de la clase, que se le debe asignar el valor de la variable al momento de invocar a un metodo de la misma
	
	public ParVariableAtributo(String variable, String atributo) {
		this.variable = variable;
		this.atributo = atributo;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getAtributo() {
		return atributo;
	}

	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}
	
}
