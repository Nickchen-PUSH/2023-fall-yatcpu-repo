work_dir=/workspaces/2023-fall-yatcpu-repo/lab3/test
binary_dir=/workspaces/2023-fall-yatcpu-repo/lab3/src/main/resources
dump_command="riscv64-linux-gnu-objdump -D -b binary -m riscv:rv64"
cd $work_dir
for file in $binary_dir/*.asmbin; do
    echo "Dumping $file"
    $dump_command $file > $file.txt
done