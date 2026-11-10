# Minecraft Ultra Launcher — início do projeto

Este é o primeiro núcleo de código-fonte do projeto. Ele ainda **não é um launcher capaz de iniciar o Minecraft**. A entrega implementa a base Android: interface inicial, perfis locais e importação de arquivos `.jar`, `.zip` e `.mrpack` para uma pasta de biblioteca. A execução do jogo, autenticação Microsoft e instalação real de versões/loaders ficam para as próximas etapas.

## Base técnica
- Kotlin + Jetpack Compose
- Android Storage Access Framework para importar arquivos sem pedir acesso amplo ao armazenamento
- Min SDK 26

## Abrir
1. Extraia o ZIP.
2. Abra a pasta `MinecraftUltraLauncher` no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Execute no emulador ou aparelho Android.

## Importação atual
- `.jar` → `files/mods`
- `.zip` → `files/resourcepacks`
- `.mrpack` → `files/modpacks`

A importação apenas organiza os arquivos. Não instala automaticamente dependências nem garante compatibilidade. Não coloque arquivos de versões diferentes no mesmo perfil.

## Próximas entregas
1. Manifesto oficial de versões e instalação Vanilla.
2. Perfis isolados e configuração de RAM.
3. Integração licenciada com runtime/launcher Java Android compatível.
4. Forge/Fabric/NeoForge e importação de modpacks.
5. Cliente PC e sincronização de perfis.
