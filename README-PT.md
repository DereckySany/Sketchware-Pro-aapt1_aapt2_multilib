<p align="center">
     <img src="assets/Sketchware-Pro.png" />
</p>

# Sketchware Pro
![GitHub contributors](https://img.shields.io/github/contributors/Sketchware-Pro/Sketchware-Pro) ![GitHub last commit](https://img.shields.io/github/last-commit/Sketchware-Pro/Sketchware-Pro) ![Discord server stats](https://img.shields.io/discord/790686719753846785)
[![Android CI](https://github.com/DereckySany/Sketchware-Pro-aapt1_aapt2_multilib/actions/workflows/android.yml/badge.svg)](https://github.com/DereckySany/Sketchware-Pro-aapt1_aapt2_multilib/actions/workflows/android.yml)

Aqui você encontrará o código-fonte de muitas classes no Sketchware Pro e, mais importante, **o
lugar** para contribuir com o Sketchware Pro.

## Construindo o aplicativo

Você deve usar o Gradle para criar o aplicativo. No entanto, é altamente recomendável usar o Android Studio.

Existem duas variantes de compilação com recursos diferentes:

  - `minApi26:` Suporta exportação de AABs de projetos, bem como compilação de código Java 1.8, 1.9, 10 e 11.
No entanto, **funciona apenas no Android 8.0 (O) e superior**.
  - `minApi21:` Não pode produzir AABs de projetos e só pode compilar código Java 1.7, mas suporta até Android 5.

Você deve selecionar a variante de compilação apropriada no Android Studio usando a guia Variantes de compilação
ou use o comando build Gradle apropriado.

### Mapa do código-fonte

| Classe | Função |
| ----- | ---- |
| `a.a.a.Dp` | Auxiliar para compilar um projeto inteiro |
| `a.a.a.Ix` | Responsável pela geração do AndroidManifest.xml |
| `a.a.a.Jx` | Gera o código fonte das atividades |
| `a.a.a.Lx` | Gera código-fonte de componentes, como ouvintes, etc. |
| `a.a.a.Boi` | Responsável pela geração dos arquivos XML dos layouts |
| `a.a.a.qq` | Registro das dependências das bibliotecas internas |
| `a.a.a.tq` | Responsável pela compilação dos questionários do diálogo |
| `a.a.a.yq` | Organiza os caminhos de arquivos dos projetos do Sketchware |

Você também pode verificar o pacote `mod` que contém a maioria das alterações dos contribuidores.

## Contribuindo

Bifurque este repositório e contribua de volta usando
[solicitações pull](https://github.com/Sketchware-Pro/Sketchware-Pro/pulls).

Quaisquer contribuições, grandes ou pequenas, recursos importantes ou correções de bugs são bem-vindas e apreciadas, mas serão
ser minuciosamente revisado.

### Como contribuir

- Bifurque o repositório para sua conta do GitHub.
- Faça uma ramificação, se necessário.
- Clone o repositório bifurcado em seu dispositivo local (opcional, você pode editar arquivos por meio da interface da web do GitHub).
- Faça alterações nos arquivos.
- (IMPORTANTE) Teste essas alterações.
- Crie uma solicitação pull neste repositório.
- Os membros do repositório revisarão sua solicitação pull e a mesclarão quando forem aceitas.

### Quais alterações (provavelmente) não aceitaremos

A maioria das alterações pode estar relacionada à interface do usuário e achamos que é mais ou menos uma perda de tempo. Se algo relacionado ao design for alterado,
idealmente, todo o aplicativo também deve seguir o novo estilo, e isso é difícil de conseguir, especialmente para mods. É por isso:

- É improvável que grandes alterações na interface do usuário (componentes que existem no Sketchware vanilla) sejam aceitas.

### Enviar mensagem

Ao fazer alterações em um ou mais arquivos, você deve *commit* esse arquivo. Você também precisa de um
*mensagem* para esse *commit*.

Você deve ler [estas diretrizes](https://www.freecodecamp.org/news/writing-good-commit-messages-a-practical-guide/) ou resumido:

- Curto e detalhado.
- Prefixe um destes tipos de confirmação:
    - `feat:` Um recurso, possivelmente melhorando algo já existente.
    - `fix:` Uma correção, por exemplo, de um bug.
    - `estilo:` Recurso e atualizações relacionadas ao estilo.
    - `refactor:` refatorando uma seção específica da base de código.
    - `teste:` Tudo relacionado a testes.
    - `docs:` Tudo relacionado a documentação.
    - `chore:` Manutenção do código (você também pode usar emojis para representar os tipos de commit).

Exemplos:
  - `feat: Acelerar a compilação com nova técnica`
  - `correção: correção de travamento durante a inicialização em determinados telefones`
  - `refator: código de reformatação em File.java`

## Obrigado por contribuir
Eles ajudam a manter o Sketchware Pro vivo. Cada contribuição aceita (útil) será anotada na atividade "Sobre os Modders". Usaremos seu nome GitHub e sua foto de perfil inicialmente, mas eles podem ser
mudou claro.

## Discórdia
Quer bater um papo conosco, falar sobre mudanças ou apenas bater um papo? Temos um servidor Discord só para isso.

[![Join our Discord server!](https://invidget.switchblade.xyz/kq39yhT4rX)](http://discord.gg/kq39yhT4rX)

## Isenção de responsabilidade
Este mod não foi feito para fins nocivos, como prejudicar o Sketchware; Muito pelo contrário, na verdade.
Foi feito para manter o Sketchware vivo pela comunidade para a comunidade. Por favor, use-o a seu próprio critério
e ser um patrocinador do Patreon deles, por exemplo. Infelizmente, todas as outras formas de apoiá-los não estão mais funcionando,
por isso é a única maneira disponível atualmente.
[Aqui está a página do Patreon.](https://www.patreon.com/sketchware)

Amamos muito o Sketchware e agradecemos aos desenvolvedores do Sketchware por criarem um aplicativo tão incrível, mas, infelizmente, não recebemos atualizações há muito tempo.
É por isso que decidimos manter o Sketchware vivo fazendo este mod, além de não exigirmos dinheiro, é totalmente gratuito :)
