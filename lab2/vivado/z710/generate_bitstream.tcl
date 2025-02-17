# Copyright 2021 Howard Lau
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

source open_project.tcl

while 1 {
    if { [catch {launch_runs synth_1 -jobs 4 } ] } {
        regexp {ERROR: \[Common (\d+-\d+)]} $errorInfo -> code
        if { [string equal $code "12-978"] } {
            puts "Already generated and up-to-date"
            break
        } elseif { [string equal $code "17-69"] } {
            puts "Out of date, reset runs"
            reset_runs synth_1
            continue
        } else {
            puts "UNKNOWN ERROR!!! $errorInfo"
            exit
        }
    }
    break
}

wait_on_run synth_1

while 1 {
    if { [catch {launch_runs impl_1 -jobs 4 -to_step write_bitstream } ] } {
        regexp {ERROR: \[Vivado (\d+-\d+)]} $errorInfo -> code
        if { [string equal $code "12-978"] } {
            puts "Already generated and up-to-date"
            break
        } elseif { [string equal $code "12-1088"] } {
            puts "Out of date, reset runs"
            reset_runs impl_1
            continue
        } else {
            puts "UNKNOWN ERROR!!! $errorInfo"
            exit
        }
    }
    break
}

wait_on_run impl_1


# export hardware platform to Vitis
set_property pfm_name {} [get_files -all $project_dir/$project_name.srcs/sources_1/bd/design_1/design_1.bd]
write_hw_platform -fixed -include_bit -force -file $project_dir/design_1_wrapper.xsa

