# Crazy Girlfriend Modpack Packager
$ErrorActionPreference = "Stop"

$root = $PSScriptRoot
if (-not $root) { $root = Get-Location }

Write-Host "Copying latest mod jar to modpack overrides..."
Copy-Item "$root\build\libs\CrazyGirlfriend-1.0.0.jar" "$root\modpack\overrides\mods\CrazyGirlfriend-1.0.0.jar" -Force

$tempPrism = Join-Path $env:TEMP "prism_pack_$([System.Guid]::NewGuid().ToString('N'))"
$tempMrpack = Join-Path $env:TEMP "mrpack_$([System.Guid]::NewGuid().ToString('N'))"

try {
    # 1. Package Prism / MultiMC / CurseForge zip
    Write-Host "Packaging Crazy-Girlfriend-Modpack-1.0.0.zip..."
    New-Item -ItemType Directory -Path $tempPrism | Out-Null
    Copy-Item "$root\modpack\instance.cfg" "$tempPrism\"
    Copy-Item "$root\modpack\manifest.json" "$tempPrism\"
    Copy-Item "$root\modpack\mmc-pack.json" "$tempPrism\"
    Copy-Item "$root\modpack\modrinth.index.json" "$tempPrism\"
    Copy-Item "$root\modpack\overrides" "$tempPrism\overrides" -Recurse
    Copy-Item "$root\modpack\overrides" "$tempPrism\.minecraft" -Recurse

    $zipDest = "$root\Crazy-Girlfriend-Modpack-1.0.0.zip"
    if (Test-Path $zipDest) { Remove-Item $zipDest -Force }
    Compress-Archive -Path "$tempPrism\*" -DestinationPath $zipDest

    # 2. Package Modrinth mrpack
    Write-Host "Packaging Crazy-Girlfriend-Modpack-1.0.0.mrpack..."
    New-Item -ItemType Directory -Path $tempMrpack | Out-Null
    Copy-Item "$root\modpack\modrinth.index.json" "$tempMrpack\"
    Copy-Item "$root\modpack\overrides" "$tempMrpack\overrides" -Recurse

    $mrpackDest = "$root\Crazy-Girlfriend-Modpack-1.0.0.mrpack"
    if (Test-Path $mrpackDest) { Remove-Item $mrpackDest -Force }
    $tempZip = Join-Path $env:TEMP "mrpack_temp_$([System.Guid]::NewGuid().ToString('N')).zip"
    Compress-Archive -Path "$tempMrpack\*" -DestinationPath $tempZip
    Move-Item $tempZip $mrpackDest -Force

    Write-Host "Packaging complete!"
    Get-Item $zipDest, $mrpackDest | Select-Object Name, Length, LastWriteTime
} finally {
    if (Test-Path $tempPrism) { Remove-Item $tempPrism -Recurse -Force -ErrorAction SilentlyContinue }
    if (Test-Path $tempMrpack) { Remove-Item $tempMrpack -Recurse -Force -ErrorAction SilentlyContinue }
}

