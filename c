#!/bin/bash
status_output=$(colima status 2>/dev/null)
echo "Colima status output:"
echo "$status_output"

if echo "$status_output" | grep -q "running"; then
    echo "Colima is already running."
else
    echo "Colima is installed but not running. Starting Colima..."
fi
