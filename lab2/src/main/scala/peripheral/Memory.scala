// Copyright 2021 Howard Lau
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package peripheral

import chisel3._
import chisel3.util._
import riscv.Parameters

class RAMBundle extends Bundle {
  val address = Input(UInt(Parameters.AddrWidth))
  val write_data = Input(UInt(Parameters.DataWidth))
  val write_enable = Input(Bool())
  val write_strobe = Input(Vec(Parameters.WordSize, Bool()))
  val read_data = Output(UInt(Parameters.DataWidth))
}
// The purpose of this module is to help the synthesis tool recognize
// our memory as a Block RAM template
class BlockRAM(capacity: Int) extends Module {
  val io = IO(new Bundle {
    val read_address = Input(UInt(Parameters.AddrWidth))
    val write_address = Input(UInt(Parameters.AddrWidth))
    val write_data = Input(UInt(Parameters.DataWidth))
    val write_enable = Input(Bool())
    val write_strobe = Input(Vec(Parameters.WordSize, Bool()))

    val debug_read_address = Input(UInt(Parameters.AddrWidth))

    val read_data = Output(UInt(Parameters.DataWidth))
    val debug_read_data = Output(UInt(Parameters.DataWidth))
  })
  val mem = SyncReadMem(capacity, Vec(Parameters.WordSize, UInt(Parameters.ByteWidth)))
  when(io.write_enable) {
    val write_data_vec = Wire(Vec(Parameters.WordSize, UInt(Parameters.ByteWidth)))
    for (i <- 0 until Parameters.WordSize) {
      write_data_vec(i) := io.write_data((i + 1) * Parameters.ByteBits - 1, i * Parameters.ByteBits)
    }
    mem.write((io.write_address >> 2.U).asUInt, write_data_vec, io.write_strobe)
  }
  io.read_data := mem.read((io.read_address >> 2.U).asUInt, true.B).asUInt
  io.debug_read_data := mem.read((io.debug_read_address >> 2.U).asUInt, true.B).asUInt
}

class Memory(capacity: Int) extends Module {
  val io = IO(new Bundle {
    val bundle = new RAMBundle

    val instruction = Output(UInt(Parameters.DataWidth))
    val instruction_address = Input(UInt(Parameters.AddrWidth))

    val debug_read_address = Input(UInt(Parameters.AddrWidth))
    val debug_read_data = Output(UInt(Parameters.DataWidth))
  })

  val mem = SyncReadMem(capacity, Vec(Parameters.WordSize, UInt(Parameters.ByteWidth)))
  when(io.bundle.write_enable) {
    val write_data_vec = Wire(Vec(Parameters.WordSize, UInt(Parameters.ByteWidth)))
    for (i <- 0 until Parameters.WordSize) {
      write_data_vec(i) := io.bundle.write_data((i + 1) * Parameters.ByteBits - 1, i * Parameters.ByteBits)
    }
    mem.write((io.bundle.address >> 2.U).asUInt, write_data_vec, io.bundle.write_strobe)
  }
  io.bundle.read_data := mem.read((io.bundle.address >> 2.U).asUInt, true.B).asUInt
  io.debug_read_data := mem.read((io.debug_read_address >> 2.U).asUInt, true.B).asUInt
  io.instruction := mem.read((io.instruction_address >> 2.U).asUInt, true.B).asUInt
}