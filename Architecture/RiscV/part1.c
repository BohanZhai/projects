#include <stdio.h> // for stderr
#include <stdlib.h> // for exit()
#include "types.h"
#include "utils.h"

void print_rtype(char *, Instruction);
void print_itype_except_load(char *, Instruction, int);
void print_load(char *, Instruction);
void print_store(char *, Instruction);
void print_branch(char *, Instruction);
void print_utype(char*, Instruction);
void print_jal(Instruction);
void print_ecall(Instruction);
void write_rtype(Instruction);
void write_itype_except_load(Instruction); 
void write_load(Instruction);
void write_store(Instruction);
void write_branch(Instruction);


void decode_instruction(uint32_t instruction_bits) {
    Instruction instruction = parse_instruction(instruction_bits); // Look in utils.c
    switch(instruction.opcode) {
        case 0x33:
            /* YOUR CODE HERE */
            write_rtype(instruction);
            break;
        case 0x13:
            /* YOUR CODE HERE */
            write_itype_except_load(instruction);
            break;
        case 0x3:
            /* YOUR CODE HERE */
            write_load(instruction);
            break;
        case 0x67:
            /* YOUR CODE HERE */
            write_itype_except_load(instruction);
            break;
        case 0x23:
            /* YOUR CODE HERE */
            write_store(instruction);
            break;
        case 0x63:
            /* YOUR CODE HERE */
            write_branch(instruction);
            break;
        case 0x37:
            /* YOUR CODE HERE */
            print_utype("lui", instruction);
            break;
        case 0x17:
            /* YOUR CODE HERE */
            print_utype("auipc", instruction);
            break;
        case 0x6F:
            /* YOUR CODE HERE */
            print_jal(instruction);
            break;
        case 0x73:
            /* YOUR CODE HERE */
            if ((instruction.itype.funct3 == 0x0) && (instruction.itype.imm == 0x0)) {
                print_ecall(instruction);
                break;
            }
        default: // undefined opcode
            handle_invalid_instruction(instruction);
            break;
    }
}

void write_rtype(Instruction instruction) {
    /* HINT: Hmmm, it's seems that there's way more R-Type instructions that funct3 possibilities... */
    switch (instruction.rtype.funct3) {
        case 0x0:
            /* YOUR CODE HERE */
            if (instruction.rtype.funct7 == 0x0) {
                print_rtype("add", instruction);
            } 
            else if (instruction.rtype.funct7 == 0x01){
                print_rtype("mul", instruction);
            }
            else if (instruction.rtype.funct7 == 0x20) {
                print_rtype("sub", instruction);
            }else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x1:
            /* YOUR CODE HERE */
            if (instruction.rtype.funct7 == 0x0) {
                print_rtype("sll", instruction);
            } 
            else if (instruction.rtype.funct7 == 0x01){
                print_rtype("mulh", instruction);
            }else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x2:
            /* YOUR CODE HERE */
            if (instruction.rtype.funct7 == 0x0) {
                print_rtype("slt", instruction);
            }else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x3:
            /* YOUR CODE HERE */
            if (instruction.rtype.funct7 == 0x0) {
                print_rtype("sltu", instruction);
            } 
            else if (instruction.rtype.funct7 == 0x01){
                print_rtype("mulhu", instruction);
            }else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x4:
            /* YOUR CODE HERE */
            if (instruction.rtype.funct7 == 0x0) {
                print_rtype("xor", instruction);
            } 
            else if (instruction.rtype.funct7 == 0x01){
                print_rtype("div", instruction);
            }else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x5:
            /* YOUR CODE HERE */
            if (instruction.rtype.funct7 == 0x0) {
                print_rtype("srl", instruction);
            } 
            else if (instruction.rtype.funct7 == 0x01){
                print_rtype("divu", instruction);
            }
            else if (instruction.rtype.funct7 == 0x20) {
                print_rtype("sra", instruction);
            }else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x6:
            /* YOUR CODE HERE */
            if (instruction.rtype.funct7 == 0x0) {
                print_rtype("or", instruction);
            } 
            else if (instruction.rtype.funct7 == 0x01){
                print_rtype("rem", instruction);
            }else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x7:
            /* YOUR CODE HERE */
            if (instruction.rtype.funct7 == 0x0) {
                print_rtype("and", instruction);
            } 
            else if (instruction.rtype.funct7 == 0x01){
                print_rtype("remu", instruction);
            }else {
                handle_invalid_instruction(instruction);
            }
            break;
        default:
            handle_invalid_instruction(instruction);
            break;
    }
}

void write_itype_except_load(Instruction instruction) {
    switch (instruction.itype.funct3) {
        case 0x0:
            /* YOUR CODE HERE */
            if (instruction.itype.opcode == 0x67) {
                print_itype_except_load("jalr",instruction, instruction.itype.imm);
                break;
            }
            print_itype_except_load("addi",instruction, instruction.itype.imm);
            break;
        case 0x1:
            /* YOUR CODE HERE */
            if (((instruction.itype.imm >> 5) & 0x7F) == 0x00) {
                printf(ITYPE_FORMAT,"slli", instruction.itype.rd, instruction.itype.rs1, instruction.itype.imm & 0x1F);
            } else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x2:
            /* YOUR CODE HERE */
            print_itype_except_load("slti", instruction, instruction.itype.imm);
            break;
        case 0x3:
            /* YOUR CODE HERE */
            print_itype_except_load("sltiu", instruction, instruction.itype.imm);
            break;
        case 0x4:
            /* YOUR CODE HERE */
            print_itype_except_load("xori", instruction, instruction.itype.imm);
            break;
        case 0x5:
            /* HINT: What makes the immediate here special? */
            /* YOUR CODE HERE */
            if (((instruction.itype.imm >> 5) & 0x7F) == 0x00) {
                printf(ITYPE_FORMAT,"srli", instruction.itype.rd, instruction.itype.rs1, instruction.itype.imm & 0x1F);
            } 

            //not sure about this function.
            else if (((instruction.itype.imm >> 5) & 0x7F) == 0x20) {
                printf(ITYPE_FORMAT,"srai", instruction.itype.rd, instruction.itype.rs1, instruction.itype.imm & 0x1F);
            } else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x6:
            /* YOUR CODE HERE */
            print_itype_except_load("ori", instruction, instruction.itype.imm);
            break;
        case 0x7:
            /* YOUR CODE HERE */
            print_itype_except_load("andi", instruction, instruction.itype.imm);
            break;
        default:
            handle_invalid_instruction(instruction);
            break;  
    }
}

void write_load(Instruction instruction) {
    switch (instruction.itype.funct3) {
        case 0x0:
            /* YOUR CODE HERE */
            print_load("lb", instruction);
            break;
        case 0x1:
            /* YOUR CODE HERE */
            print_load("lh", instruction);
            break;
        case 0x2:
            /* YOUR CODE HERE */
            print_load("lw", instruction);
            break;
        case 0x4:
            /* YOUR CODE HERE */
            print_load("lbu", instruction);
            break;
        case 0x5:
            /* YOUR CODE HERE */
            print_load("lhu", instruction);
            break;
        default:
            handle_invalid_instruction(instruction);
            break;
    }
}

void write_store(Instruction instruction) {
    switch (instruction.stype.funct3) {
        case 0x0:
            /* YOUR CODE HERE */
            print_store("sb", instruction);
            break;
        case 0x1:
            /* YOUR CODE HERE */
            print_store("sh", instruction);
            break;
        case 0x2:
            /* YOUR CODE HERE */
            print_store("sw", instruction);
            break;
        default:
            handle_invalid_instruction(instruction);
            break;
    }
}

void write_branch(Instruction instruction) {
    switch (instruction.sbtype.funct3) {
        case 0x0:
            /* YOUR CODE HERE */
            print_branch("beq", instruction);
            break;
        case 0x1:
            /* YOUR CODE HERE */
            print_branch("bne", instruction);
            break;
        case 0x4:
            /* YOUR CODE HERE */
            print_branch("blt", instruction);
            break;
        case 0x5:
            /* YOUR CODE HERE */
            print_branch("bge", instruction);
            break;
        case 0x6:
            /* YOUR CODE HERE */
            print_branch("bltu", instruction);
            break;
        case 0x7:
            /* YOUR CODE HERE */
            print_branch("bgeu", instruction);
            break;
        default:
            handle_invalid_instruction(instruction);
            break;
    }
}

/* utils.c and utils.h might be useful here... */

void print_utype(char* name, Instruction instruction) {
    /* YOUR CODE HERE */
    printf(UTYPE_FORMAT, name, instruction.utype.rd, instruction.utype.imm);
}

void print_jal(Instruction instruction) {
    /* YOUR CODE HERE */
    printf(JAL_FORMAT, instruction.ujtype.rd, get_jump_offset(instruction));
}

void print_ecall(Instruction instruction) {
    /* YOUR CODE HERE */
    printf(ECALL_FORMAT);
}

void print_rtype(char *name, Instruction instruction) {
    /* YOUR CODE HERE */
    printf(RTYPE_FORMAT, name, instruction.rtype.rd, instruction.rtype.rs1, instruction.rtype.rs2);
}

void print_itype_except_load(char *name, Instruction instruction, int imm) {
    /* YOUR CODE HERE */
    printf(ITYPE_FORMAT, name, instruction.itype.rd, instruction.itype.rs1, sign_extend_number(instruction.itype.imm, 12));
}

void print_load(char *name, Instruction instruction) {
    /* YOUR CODE HERE */
    printf(MEM_FORMAT, name, instruction.itype.rd, sign_extend_number(instruction.itype.imm, 12), instruction.itype.rs1);
}

void print_store(char *name, Instruction instruction) {
    /* YOUR CODE HERE */
    printf(MEM_FORMAT, name, instruction.stype.rs2, get_store_offset(instruction), instruction.stype.rs1);
}

void print_branch(char *name, Instruction instruction) {
    /* YOUR CODE HERE */
    printf(BRANCH_FORMAT, name, instruction.sbtype.rs1, instruction.sbtype.rs2, get_branch_offset(instruction));
}
