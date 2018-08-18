#include <stdio.h> // for stderr
#include <stdlib.h> // for exit()
#include "types.h"
#include "utils.h"
#include "riscv.h"

void execute_rtype(Instruction, Processor *);
void execute_itype_except_load(Instruction, Processor *);
void execute_branch(Instruction, Processor *);
void execute_jal(Instruction, Processor *);
void execute_jalr(Instruction, Processor *);
void execute_load(Instruction, Processor *, Byte *);
void execute_store(Instruction, Processor *, Byte *);
void execute_ecall(Processor *, Byte *);
void execute_lui(Instruction, Processor *);
void execute_auipc(Instruction, Processor *);

void execute_instruction(uint32_t instruction_bits, Processor *processor,Byte *memory) {    
    Instruction instruction = parse_instruction(instruction_bits); // Look in utils.c
    switch(instruction.opcode) {
        case 0x33:
            /* YOUR CODE HERE */
            execute_rtype(instruction, processor); // this is rtype
            break;
        case 0x13:
            /* YOUR CODE HERE */
            execute_itype_except_load(instruction, processor);
            //this is itype
            break;
        case 0x3:
            /* YOUR CODE HERE */
            execute_load(instruction, processor, memory); //memory
            break;
        case 0x67:
            /* YOUR CODE HERE */
            // this is jalr
            execute_jalr(instruction, processor);

            break;
        case 0x23:
            /* YOUR CODE HERE */
            execute_store(instruction, processor, memory);
            break;
        case 0x63:
            /* YOUR CODE HERE */
            execute_branch(instruction, processor);
            break;
        case 0x37:
            /* YOUR CODE HERE */
            execute_lui(instruction, processor);
            break;
        case 0x17:
            /* YOUR CODE HERE */
            execute_auipc(instruction, processor);
            break;
        case 0x6F:
            /* YOUR CODE HERE */
            execute_jal(instruction, processor);
            break;
        case 0x73:
            /* YOUR CODE HERE */
            execute_ecall(processor, memory);
            break;
        default: // undefined opcode
            handle_invalid_instruction(instruction);
            break;
    }
}

void execute_rtype(Instruction instruction, Processor *processor) {
    uint32_t rd = instruction.rtype.rd;
    uint32_t rs1 = instruction.rtype.rs1;
    uint32_t rs2 = instruction.rtype.rs2;
    switch (instruction.rtype.funct3){
        case 0x0:
            /* YOUR CODE HERE */

            // which function do we need to use here?
            if ((rs1 > 31) | (rs2 > 31) | (rd > 31)) {
                handle_invalid_instruction(instruction);
                return;
            }


            if (instruction.rtype.funct7 == 0x00) {
                int32_t rs1_value = processor -> R[rs1];
                int32_t rs2_value = processor -> R[rs2];
                int32_t ans = rs1_value + rs2_value;
                processor -> R[rd] = ans;
            }
            else if (instruction.rtype.funct7 == 0x01) {
                int32_t rs1_value = (processor -> R[rs1]);
                int32_t rs2_value = (processor -> R[rs2]);
                int32_t ans = rs1_value * rs2_value;
                (processor -> R)[rd] = ans;
            }
            else if (instruction.rtype.funct7 == 0x20) {
                int32_t rs1_value = (processor -> R)[rs1];
                int32_t rs2_value = (processor -> R)[rs2];
                int32_t ans = (rs1_value - rs2_value);
                (processor -> R)[rd] = ans;
            }
            (processor -> PC) += 4;
            break;
        case 0x1:
            /* YOUR CODE HERE */
            if (instruction.rtype.funct7 == 0x01) {
                // mulh
                int64_t ans = 0;
                int64_t rs1_value = (processor -> R)[rs1];
                int64_t rs2_value = (processor -> R)[rs2];
                rs1_value <<= 32;
                rs1_value >>= 32;
                rs2_value <<= 32;
                rs2_value >>= 32;
                ans = rs1_value * rs2_value;
                uint64_t uans = ans;
                (processor -> R)[rd] = (uans >> 32);
                (processor -> PC) += 4;
            }
            else if (instruction.rtype.funct7 == 0x00) {
                // R[rd] ← R[rs1] << R[rs2]
                uint32_t rs2_lastfive = ((processor -> R)[rs2]) & 0x1F;

                (processor -> R)[rd] = ((processor -> R[rs1]) << rs2_lastfive);
                (processor -> PC) += 4;
            } else {
                handle_invalid_instruction(instruction);
                return;
            }
            
            break;
        case 0x2:
            /* YOUR CODE HERE */
            // slt
            /*if (rs1 > 31 | rs2 > 31 | rd > 31) {
                handle_invalid_instruction(instruction);
                return
            }*/
            if (instruction.rtype.funct7 == 0x00) {
                int32_t rs1_value = (processor -> R)[rs1];
                int32_t rs2_value = (processor -> R)[rs2];
                (processor -> R)[rd] = (rs1_value < rs2_value ? 1 : 0);
                (processor -> PC) += 4;
            } else {
                handle_invalid_instruction(instruction);
                return;
            }
            break;
        case 0x3:
            /* YOUR CODE HERE */
            //sltu unsigned comparsion
            /*if (rs1 > 31 | rs2 > 31 | rd > 31) {
                handle_invalid_instruction(instruction);
                return;
            }*/
            if (instruction.rtype.funct7 == 0x00) {
                uint32_t rs1_value = (processor -> R)[rs1];
                uint32_t rs2_value = (processor -> R)[rs2];
                (processor -> R)[rd] = (rs1_value < rs2_value ? 1 : 0);
                (processor -> PC) += 4;
            }
            else if (instruction.rtype.funct7 == 0x01) {
                uint64_t ans = 0;
                uint64_t rs1_value = (processor -> R)[rs1];
                uint64_t rs2_value = (processor -> R)[rs2];
                ans = rs1_value * rs2_value;
                uint64_t uans = ans;
                (processor -> R)[rd] = (uans >> 32);
                (processor -> PC) += 4;
            } else {
                handle_invalid_instruction(instruction);
                return;
            }
            break;
        case 0x4:
            /* YOUR CODE HERE */
            /*if (rs1 > 31 | rs2 > 31 | rd > 31) {
                handle_invalid_instruction(instruction);
                return;
            }*/
            if (instruction.rtype.funct7 == 0x00) {
                uint32_t rs1_value = (processor -> R)[rs1];
                uint32_t rs2_value = (processor -> R)[rs2];
                (processor -> R)[rd] = (rs1_value ^ rs2_value);
                (processor -> PC) += 4;
            }
            else if (instruction.rtype.funct7 == 0x01) {
                int32_t rs1_value = (processor -> R)[rs1];
                int32_t rs2_value = (processor -> R)[rs2];
                (processor -> R)[rd] = (rs1_value / rs2_value);
                (processor -> PC) += 4;
            }
            break;
        case 0x5:
            /* YOUR CODE HERE */
            // 0x00 srl  logic    R[rd] ← R[rs1] >> R[rs2]
            // 0x01 divu rd, rs1, rs2  R[rd] ← R[rs1] / R[rs2] // unsigned operation
            // 0x20 sra rd, rs1, rs2  arthmetic   R[rd] ← R[rs1] >> R[rs2]
            /*if (rs1 > 31 | rs2 > 31 | rd > 31) {
                handle_invalid_instruction(instruction);
                return;
            }*/
            if (instruction.rtype.funct7 == 0x00) {
                uint32_t rs1_value = (processor -> R)[rs1];
                uint32_t rs2_value = ((processor -> R)[rs2]) & 0x1F ;
                (processor -> R)[rd] = rs1_value >> rs2_value;
                (processor -> PC) += 4;
            }
            else if (instruction.rtype.funct7 == 0x01) {
                uint32_t rs1_value = (processor -> R)[rs1];
                uint32_t rs2_value = (processor -> R)[rs2];
                (processor -> R)[rd] = rs1_value / rs2_value;
                (processor -> PC) += 4;
            }
            else if (instruction.rtype.funct7 == 0x20) {
                int32_t rs1_value = (processor -> R)[rs1];
                int32_t rs2_value = ((processor -> R)[rs2]) & 0x1F;
                (processor -> R)[rd] = (rs1_value >> rs2_value);
                (processor -> PC) += 4;
            } else {
                handle_invalid_instruction(instruction);
            }
            break;
        case 0x6:
            /* YOUR CODE HERE */
            // or rd, rs1, rs2.  0x00.  R[rd] ← R[rs1] | R[rs2]
            // rem rd, rs1, rs2. 0x01.  R[rd] ← R[rs1] % R[rs2]
            /*if (rs1 > 31 | rs2 > 31 | rd > 31) {
                handle_invalid_instruction(instruction);
                return;
            }*/
            if (instruction.rtype.funct7 == 0x00) {
                ((processor -> R)[rd]) = ((processor -> R[rs1]) | (processor -> R[rs2]));
                (processor -> PC) += 4;
            }
            else if (instruction.rtype.funct7 == 0x01) {
                int32_t rs1_value = (processor -> R)[rs1];
                int32_t rs2_value = (processor -> R)[rs2];
                ((processor -> R)[rd]) = (rs1_value % rs2_value);
                (processor -> PC) += 4;
            }
            break;
        case 0x7:
            /* YOUR CODE HERE */
            // and rd, rs1, rs2.  0x00.  R[rd] ← R[rs1] & R[rs2]
            // remu rd, rs1, rs2. 0x01   R[rd] ← R[rs1] % R[rs2] // unsigned operation

            if (instruction.rtype.funct7 == 0x00) {
                ((processor -> R)[rd]) = ((processor -> R[rs1]) & (processor -> R[rs2]));
                (processor -> PC) += 4;
            }
            else if (instruction.rtype.funct7 == 0x01) {
                ((processor -> R)[rd]) = ((processor -> R[rs1]) % (processor -> R[rs2]));
                (processor -> PC) += 4;
            }
            break;
        default:
            handle_invalid_instruction(instruction);
            exit(-1);
            break;
    }
}

void execute_itype_except_load(Instruction instruction, Processor *processor) {
    unsigned int rd = instruction.itype.rd;
    unsigned int rs1 = instruction.itype.rs1;
    int32_t rs1_value = ((processor -> R)[rs1]);
    int32_t imm = sign_extend_number(instruction.itype.imm, 12);
    uint32_t immu = instruction.itype.imm;
    uint32_t rs1_valueu = (processor -> R[rs1]);
    uint32_t imm_r = instruction.itype.imm & 0x1F;
    // change PC to add 4 each time.
    switch (instruction.itype.funct3) {
        
        case 0x0:
            /* YOUR CODE HERE */
            // addi rd, rs1, imm      R[rd] ← R[rs1] + imm
            (processor -> PC) += 4;
            ((processor -> R)[rd]) = rs1_value + imm;
            break;
        case 0x1:
            /* YOUR CODE HERE */
            // slli rd, rs1, imm.  0x00 see the case in part1
            if (((instruction.itype.imm >> 5) & 0x7F) == 0x00) {
                uint32_t im = instruction.itype.imm & 0x1F;
                ((processor -> R)[rd]) = rs1_value << im;
                (processor -> PC) += 4;
            } else {
                handle_invalid_instruction(instruction);
                exit(-1);
                break;
            }
            break;
        case 0x2:
            /* YOUR CODE HERE */
            // slti rd, rs1, imm       R[rd] ← (R[rs1] < imm) ? 1 : 0
            (processor -> PC) += 4;
            //int32_t imm = sign_extend_number(instruction.itype.imm, 12);
            //int32_t rs1_value = (processor -> R[rs1]);
            ((processor -> R)[rd]) = ((rs1_value < imm) ? 1 : 0);
            
            break;
        case 0x3:
            /* YOUR CODE HERE */
            // sltiu rd, rs1, imm.    R[rd] ← (R[rs1] < imm) ? 1 : 0 // unsigned comparison
            (processor -> PC) += 4;
            ((processor -> R)[rd]) = ((rs1_valueu < immu) ? 1 : 0);
            break;
        case 0x4:
            /* YOUR CODE HERE */
            // xori rd, rs1, imm.    R[rd] ← R[rs1] ^ imm
            (processor -> PC) += 4;
            ((processor -> R)[rd]) = (rs1_value ^ imm);
            break;
        case 0x5:
            /* YOUR CODE HERE */
            // srli rd, rs1, imm.  0x00 see part1  R[rd] ← R[rs1] >> imm
            // srai rd, rs1, imm.  0x20 see part1  R[rd] ← R[rs1] >> imm
            if (((instruction.itype.imm >> 5) & 0x7F) == 0x00) {
               
                ((processor -> R)[rd]) = (rs1_valueu >> imm_r);
                (processor -> PC) += 4;
            }
            else if (((instruction.itype.imm >> 5) & 0x7F) == 0x20) {
                
                ((processor -> R)[rd]) = (rs1_value >> imm_r);
                (processor -> PC) += 4;
            } else {
                handle_invalid_instruction(instruction);
                exit(-1);
                break;
            }
            break;
        case 0x6:
            /* YOUR CODE HERE */
            // ori rd, rs1, imm.  R[rd] ← R[rs1] | imm
            (processor -> PC) += 4;
            ((processor -> R)[rd]) = (rs1_value | imm);
            break;
        case 0x7:
            /* YOUR CODE HERE */
            // andi rd, rs1, imm   R[rd] ← R[rs1] & imm
            (processor -> PC) += 4;
            ((processor -> R)[rd]) = (rs1_value & imm);
            break;
        default:
            handle_invalid_instruction(instruction);
            break;
    }
}

void execute_ecall(Processor *p, Byte *memory) {

    Register i;
    
    // What do we switch on?
    switch(/* YOUR CODE HERE */p -> R[10]) {       // a0 will determin which case!!!
        case 1: // print an integer
            printf("%d",p->R[11]);
            (p -> PC) += 4;
            break;
        case 4: // print a string
            for(i = p->R[11]; i < MEMORY_SPACE && load(memory, i, LENGTH_BYTE); i++) {
                printf("%c",load(memory,i,LENGTH_BYTE));
            }
            (p -> PC) += 4;
            break;
        case 10: // exit
            printf("exiting the simulator\n");
            exit(0);
            break;
        case 11: // print a character
            printf("%c",p->R[11]);
            (p -> PC) += 4;
            break;
        default: // undefined ecall
            printf("Illegal ecall number %d\n", p->R[10]);
            exit(-1);
            break;
    }
}

void execute_branch(Instruction instruction, Processor *processor) {
    int offset = get_branch_offset(instruction);
    unsigned int rs1 = instruction.sbtype.rs1;
    unsigned int rs2 = instruction.sbtype.rs2;
    int32_t rs1_value = (processor -> R[rs1]);
    int32_t rs2_value = (processor -> R[rs2]);
    switch (instruction.sbtype.funct3) {
        case 0x0:
            /* YOUR CODE HERE */
            // beq rs1, rs2, offset   if(R[rs1] == R[rs2])  PC ← PC + {offset, 1b'0}
            if (rs1_value == rs2_value) {
                (processor -> PC) += offset;
            }
            else {
                (processor -> PC) += 4;
            }
            break;
        case 0x1:
            /* YOUR CODE HERE */
            // bne rs1, rs2, offset.  0x1.  if(R[rs1] != R[rs2]).  PC ← PC + {offset, 1b'0}
            if (rs1_value != rs2_value) {
                (processor -> PC) += offset; 
            } else {
                (processor -> PC) += 4;
            }
            break;
        case 0x4:
            /* YOUR CODE HERE */
            // blt rs1, rs2, offset.  0x4.  if(R[rs1] < R[rs2]).   PC ← PC + {offset, 1b'0}
            if (rs1_value < rs2_value) {
                (processor -> PC) += offset;
            } else {
                (processor -> PC) += 4;
            }
            break;
        case 0x5:
            /* YOUR CODE HERE */
            // bge rs1, rs2, offset.  0x5.  if(R[rs1] >= R[rs2]).  PC ← PC + {offset, 1b'0}
            if (rs1_value >= rs2_value) {
                (processor -> PC) += offset;
            } else {
                (processor -> PC) += 4;
            }

            break;
        case 0x6:
            /* YOUR CODE HERE */
            // bltu rs1, rs2, offset. 0x6.  if(R[rs1] < R[rs2]) // unsigned comparison   PC ← PC + {offset, 1b'0}
            if ((processor -> R[rs1]) < (processor -> R[rs2])) {
                (processor -> PC) += offset;
            } else {
                (processor -> PC) += 4;
            }
            break;
        case 0x7:
            /* YOUR CODE HERE */
            // bgeu rs1, rs2, offset. 0x7.  if(R[rs1] >= R[rs2]) // unsigned comparison. PC ← PC + {offset, 1b'0}
            if ((processor -> R[rs1]) >= (processor -> R[rs2])) {
                (processor -> PC) += offset;
            } else {
                (processor -> PC) += 4;
            }
            break;
        default:
            handle_invalid_instruction(instruction);
            exit(-1);
            break;
    }
}

void execute_load(Instruction instruction, Processor *processor, Byte *memory) {
    unsigned int rs1 = instruction.itype.rs1;
    unsigned int address = (processor -> R[rs1]);
    unsigned int rd = instruction.itype.rd;
    int imm = sign_extend_number(instruction.itype.imm, 12);
    switch (instruction.itype.funct3) {
         // offset
        case 0x0:
            /* YOUR CODE HERE */
            // lb rd, offset(rs1). R[rd] ← SignExt(Mem(R[rs1] + offset, byte))
            ((processor -> R)[rd]) = load(memory, (address + imm), LENGTH_BYTE);
            (processor -> PC) += 4;
            break;
        case 0x1:
            /* YOUR CODE HERE */
            // lh rd, offset(rs1). R[rd] ← SignExt(Mem(R[rs1] + offset, half))
            (processor -> PC) += 4;
            (processor -> R[rd]) = load(memory, (address + imm), LENGTH_HALF_WORD) ;
            break;
        case 0x2:
            /* YOUR CODE HERE */
            // lw rd, offset(rs1). R[rd] ← Mem(R[rs1] + offset, word)
            (processor -> R[rd]) = load(memory, (address + imm), LENGTH_WORD);
            (processor -> PC) += 4;
            break;
        case 0x4:
            /* YOUR CODE HERE */
            // lbu rd, offset(rs1). R[rd] ← {24'b0, Mem(R[rs1] + offset, byte)}
            // not sure about this guide
            (processor -> R[rd]) = memory[(processor -> R[rs1]) + imm];
            (processor -> PC) += 4;

            break;
        case 0x5:
            /* YOUR CODE HERE */
            // lhu rd, offset(rs1). R[rd] ← {16'b0, Mem(R[rs1] + offset, half)}
            (processor -> PC) += 4;
            uint16_t ans = memory[((processor -> R)[rs1]) + imm];
            uint16_t pre = memory[((processor -> R)[rs1]) + imm + 1];
            pre <<= 8;
            ans = ans | pre;
            ((processor -> R)[rd]) = ans;
            break;
        default:
            handle_invalid_instruction(instruction);
            break;
    }
}

void execute_store(Instruction instruction, Processor *processor, Byte *memory) {
    int offset = get_store_offset(instruction);
    unsigned int rs1 = instruction.stype.rs1;
    unsigned int rs2 = instruction.stype.rs2;
    unsigned int address = (processor -> R[rs1]) + offset;
    Word value = (processor -> R[rs2]);
    switch (instruction.stype.funct3) {

        case 0x0:
            /* YOUR CODE HERE */
            // sb rs2, offset(rs1)  Mem(R[rs1] + offset) ← R[rs2][7:0]
            store(memory, address, LENGTH_BYTE, value);
            (processor -> PC) += 4;
            break;
        case 0x1:
            /* YOUR CODE HERE */
            // sh rs2, offset(rs1)  Mem(R[rs1] + offset) ← R[rs2][15:0]
            store(memory, address, LENGTH_HALF_WORD, value);
            (processor -> PC) += 4;
            break;
        case 0x2:
            /* YOUR CODE HERE */
            // sw rs2, offset(rs1).  Mem(R[rs1] + offset) ← R[rs2]
            store(memory, address, LENGTH_WORD, value);
            (processor -> PC) += 4;
            break;
        default:
            handle_invalid_instruction(instruction);
            exit(-1);
            break;
    }
}

void execute_jal(Instruction instruction, Processor *processor) {
    /* YOUR CODE HERE */
    int offset = get_jump_offset(instruction);
    unsigned int rd = instruction.ujtype.rd;
    (processor -> R[rd]) = (processor -> PC) + 4;
    (processor -> PC) += offset;

}

void execute_jalr(Instruction instruction, Processor *processor) {
    /* YOUR CODE HERE */
    int32_t offset = sign_extend_number(instruction.itype.imm, 12);
    uint32_t rd = instruction.itype.rd;
    uint32_t rs1 = instruction.itype.rs1;
    (processor -> R[rd]) = (processor -> PC) + 4;
    (processor -> PC) = (processor -> R[rs1]) + offset;
}

void execute_lui(Instruction instruction, Processor *processor) {
    /* YOUR CODE HERE */
    uint32_t ans = instruction.utype.imm;
    ans <<= 12;
    uint32_t rd = instruction.itype.rd;
    (processor -> R[rd]) = ans;
    (processor -> PC) += 4;

}

void execute_auipc(Instruction instruction, Processor *processor) {
    /* YOUR CODE HERE */
    uint32_t ans = instruction.utype.imm;
    ans <<= 12;
    uint32_t rd = instruction.itype.rd;
    (processor -> R[rd]) = ans + (processor -> PC);
    (processor -> PC) += 4;
}

void store(Byte *memory, Address address, Alignment alignment, Word value) {
    /* YOUR CODE HERE */
    for (int i = 0; i < alignment; i++) {
        memory[address + i] = value & 0xFF;
        value >>= 8;
    }

}

Word load(Byte *memory, Address address, Alignment alignment) {
    /* YOUR CODE HERE */
    /*This function we want get the element store in address to address + alignment in memory*/
    Word ans = 0x0;
    // because each time we pass 8 bits in ans, and move 8 bits, and we need to avoid extra move
    for (int i = alignment - 1; i > 0; i--) {
        ans = (ans | (memory[address + i]));
        ans = ans << 8;
    }
    ans = (ans | (memory[address]));
    ans = sign_extend_number(ans, alignment*8);
    return ans;
}
