package assembler;

public class Instruction {
    String label,operation,operand,address,objectcode;

    public Instruction(String label, String operation, String operand, String address, String objectcode) {
        this.label = label;
        this.operation = operation;
        this.operand = operand;
        this.address = address;
        this.objectcode = objectcode;
    }
    public Instruction(String label, String operation, String operand, String address) {
        this.label = label;
        this.operation = operation;
        this.operand = operand;
        this.address = address;
    }
    
    public Instruction(String label, String operation, String operand) {
        this.label = label;
        this.operation = operation;
        this.operand = operand;
        
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getObjectcode() {
        return objectcode;
    }

    public void setObjectcode(String objectcode) {
        this.objectcode = objectcode;
    }
}