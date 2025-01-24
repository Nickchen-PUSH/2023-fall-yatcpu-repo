work_dir=/workspaces/2023-fall-yatcpu-repo/example_objdump
binary_dir=/workspaces/2023-fall-yatcpu-repo/lab2/src/main/resources
dump_command="riscv64-linux-gnu-objdump -D -b binary -m riscv:rv64"
cd $work_dir
for file in $binary_dir/*.asmbin; do
    name=$(basename $file)
    echo "Dumping $file"
    $dump_command $file > $work_dir/$name.txt
done