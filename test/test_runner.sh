#!/bin/bash

# Basic testing strategy for Lox
# generate output in test/actual and compare against test/expected

# Change to the directory where this script is located
cd "$(dirname "$0")"
mkdir -p actual

# Track test results
passed=0
failed=0
total=0

# Iterate over all .lox files in the test directory
for file in *.lox; do
    # Extract filename without path and extension
    filename=$(basename "$file" .lox)
    total=$((total + 1))

    # Run jlox and save output to _actual.txt file (overwrite existing)
    echo "Running jlox on $file..."
    ../jlox "$file" >| "actual/${filename}_actual.txt" 2>&1
    jlox_exit_code=$?

    # Always report that output was saved (even if jlox had errors)
    echo "✓ Output saved to actual/${filename}_actual.txt"

    # Compare with expected output if it exists
    expected_file="expected/${filename}_expected.txt"
    actual_file="actual/${filename}_actual.txt"

    if [ -f "$expected_file" ]; then
        if diff -q "$expected_file" "$actual_file" > /dev/null 2>&1; then
            echo "✓ $filename: PASSED"
            passed=$((passed + 1))
        else
            echo "✗ $filename: FAILED - Output mismatch"
            echo "  Expected: $expected_file"
            echo "  Actual:   $actual_file"
            echo "  Diff:"
            diff "$expected_file" "$actual_file" | sed 's/^/    /'
            failed=$((failed + 1))
        fi
    else
        echo "? $filename: No expected output file found"
    fi
    echo
done

# Print summary
echo "=========================================="
echo "Test Summary:"
echo "Total:  $total"
echo "Passed: $passed"
echo "Failed: $failed"
echo "=========================================="

if [ $failed -eq 0 ]; then
    echo "✅ All tests passed!"
    exit 0
else
    echo "❌ $failed test(s) failed"
    exit 1
fi
