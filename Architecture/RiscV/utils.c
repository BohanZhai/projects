#include "utils.h"
#include <stdio.h>
#include <stdlib.h>

/* Sign extends the given field to a 32-bit integer where field is
 * interpreted an n-bit integer. Look in test_utils.c for examples. */ 
int sign_extend_number( unsigned int field, unsigned int n) {
    /* YOUR CODE HERE */
    unsigned int a;
    unsigned int answer;
    answer = field;
    a = 1 & (field >> (n - 1));
    for (int i = 0; i < (32 - n); i++) {
        answer = answer | (a << (n + i));
    }
    return answer;
}

/* Unpacks the 32-bit machine code instruction given into the correct
 * type within the instruction struct. Look at types.h */ 
Instruction parse_instruction(uint32_t instruction_bits) {
    Instruction instruction;
    /* YOUR CODE HERE */
    unsigned int opcd = instruction_bits & 0x7F;
    unsigned int steps = instruction_bits;
    switch(opcd) {
        case 0x33:
            /* YOUR CODE HERE */
            instruction.rtype.opcode = opcd;
            steps >>= 7;
            instruction.rtype.rd = steps & 0x1F;
            steps >>= 5;
            instruction.rtype.funct3 = steps & 0x7;
            steps >>= 3;
            instruction.rtype.rs1 = steps & 0x1F;
            steps >>= 5;
            instruction.rtype.rs2 = steps & 0x1F;
            steps >>= 5;
            instruction.rtype.funct7 = steps;
            break;
        case 0x13:
            /* YOUR CODE HERE */
            instruction.itype.opcode = opcd;
            steps >>= 7;
            instruction.itype.rd = steps & 0x1F;
            steps >>= 5;
            instruction.itype.funct3 = steps & 0x7;
            steps >>= 3;
            instruction.itype.rs1 = steps & 0x1F;
            steps >>= 5;
            instruction.itype.imm = steps;
            break;
        case 0x3:
            /* YOUR CODE HERE */
            instruction.itype.opcode = opcd;
            steps >>= 7;
            instruction.itype.rd = steps & 0x1F;
            steps >>= 5;
            instruction.itype.funct3 = steps & 0x7;
            steps >>= 3;
            instruction.itype.rs1 = steps & 0x1F;
            steps >>= 5;
            instruction.itype.imm = steps;
            break;
        case 0x67:
            /* YOUR CODE HERE */
            instruction.itype.opcode = opcd;
            steps >>= 7;
            instruction.itype.rd = steps & 0x1F;
            steps >>= 5;
            instruction.itype.funct3 = steps & 0x7;
            steps >>= 3;
            instruction.itype.rs1 = steps & 0x1F;
            steps >>= 5;
            instruction.itype.imm = steps;
            break;

        case 0x23:
            /* YOUR CODE HERE */
            instruction.stype.opcode = opcd;
            steps >>= 7;
            instruction.stype.imm5 = steps & 0x1F;
            steps >>= 5;
            instruction.stype.funct3 = steps & 0x7;
            steps >>= 3;
            instruction.stype.rs1 = steps & 0x1F;
            steps >>= 5;
            instruction.stype.rs2 = steps & 0x1F;
            steps >>= 5;
            instruction.stype.imm7 = steps;
            break;
        case 0x63:
            /* YOUR CODE HERE */
            instruction.sbtype.opcode = opcd;
            steps >>= 7;
            instruction.sbtype.imm5 = steps & 0x1F;
            steps >>= 5;
            instruction.sbtype.funct3 = steps & 0x7;
            steps >>= 3;
            instruction.sbtype.rs1 = steps & 0x1F;
            steps >>= 5;
            instruction.sbtype.rs2 = steps & 0x1F;
            steps >>= 5;
            instruction.sbtype.imm7 = steps;
            break;
        case 0x37:
            /* YOUR CODE HERE */
            instruction.utype.opcode = opcd;
            steps >>= 7;
            instruction.utype.rd = steps & 0x1F;
            steps >>= 5;
            instruction.utype.imm = steps;
            break;
        case 0x17:
            /* YOUR CODE HERE */
            instruction.utype.opcode = opcd;
            steps >>= 7;
            instruction.utype.rd = steps & 0x1F;
            steps >>= 5;
            instruction.utype.imm = steps;
            break;
        case 0x6F:
            /* YOUR CODE HERE */
            instruction.ujtype.opcode = opcd;
            steps >>= 7;
            instruction.ujtype.rd = steps & 0x1F;
            steps >>= 5;
            instruction.ujtype.imm = steps;
            break;
        case 0x73:
            /* YOUR CODE HERE */
            instruction.itype.opcode = opcd;
            steps >>= 7;
            instruction.itype.rd = steps & 0x1F;
            steps >>= 5;
            instruction.itype.funct3 = steps & 0x7;
            steps >>= 3;
            instruction.itype.rs1 = steps & 0x1F;
            steps >>= 5;
            instruction.itype.imm = steps;
            break;
        default: // undefined opcode
            instruction.opcode = opcd;
            steps >>= 7;
            instruction.rest = steps;
            break;

    }
    return instruction;
}

/* Return the number of bytes (from the current PC) to the branch label using the given
 * branch instruction */
int get_branch_offset(Instruction instruction) {
    /* YOUR CODE HERE */
    int eleven = instruction.sbtype.imm5 & 1;
    int ans = (instruction.sbtype.imm5 >> 1) | ((instruction.sbtype.imm7 & 0x3F) << 4) | (eleven << 10) | ((instruction.sbtype.imm7 >> 6) << 11);
    ans = ans << 1;
    ans = sign_extend_number(ans, 13);
    return ans; 
}

/* Returns the number of bytes (from the current PC) to the jump label using the given
 * jump instruction */
int get_jump_offset(Instruction instruction) {
    /* YOUR CODE HERE */
    int tot = instruction.ujtype.imm;
    int one_to_ten = (tot >> 9) & 0x3FF;
    int twenty = (tot >> 19);
    int twelve_to_ninteen = tot & 0xFF;
    int eleven = (tot >> 8) & 1;
    int ans = one_to_ten | (eleven << 10) | (twelve_to_ninteen << 11) | (twenty << 19);
    ans <<= 1;
    ans = sign_extend_number(ans, 21);
    return ans;
}

/* Returns the byte offset (from the address in rs2) for storing info using the given
 * store instruction */ 
int get_store_offset(Instruction instruction) {
    /* YOUR CODE HERE */
    int ans = instruction.stype.imm5 | (instruction.stype.imm7 << 5);
    ans = sign_extend_number(ans, 12);
    return ans;
}

void handle_invalid_instruction(Instruction instruction) {
    printf("Invalid Instruction: 0x%08x\n", instruction.bits); 
}

void handle_invalid_read(Address address) {
    printf("Bad Read. Address: 0x%08x\n", address);
    exit(-1);
}

void handle_invalid_write(Address address) {
    printf("Bad Write. Address: 0x%08x\n", address);
    exit(-1);
}

