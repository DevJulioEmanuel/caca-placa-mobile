# Caça Placa - Mobile (Android) 🚏

Aplicativo Android do projeto social **Caça Placa**, voltado ao mapeamento colaborativo e georreferenciado de sinalizações de trânsito no município de **Quixadá, Ceará**.

O foco central é a **resiliência em áreas com baixa conectividade**: agentes e colaboradores conseguem registrar placas, capturar fotos e coordenadas geográficas sem depender de internet no momento da coleta. Os dados são sincronizados automaticamente depois.

---

## 📑 Sumário

- [Funcionalidades](#-funcionalidades)
- [Stack tecnológica](#️-stack-tecnológica)
- [Arquitetura](#️-arquitetura)
- [Fluxo de sincronização offline-first](#-fluxo-de-sincronização-offline-first)
- [Como executar localmente](#-como-executar-localmente)
- [Projetos relacionados](#-projetos-relacionados)

---

## 📱 Funcionalidades

| Recurso | Descrição |
|---|---|
| **Sincronização offline-first** | Os registros são salvos localmente e enviados em segundo plano assim que a conexão é restaurada. |
| **Geolocalização precisa** | Coordenadas capturadas no momento do registro, cruzadas com a malha viária (validação via PostGIS no backend). |
| **Captura de imagens** | Integração nativa com a câmera para registrar o estado de conservação da placa, com envio otimizado via `multipart/form-data`. |
| **Autenticação segura** | Sessão baseada em JWT, com persistência segura do token para operações em background. |

---

## 🛠️ Stack tecnológica

Construído com ferramentas modernas do ecossistema Android nativo, seguindo os princípios de **Clean Code** e **SOLID**.

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin 2.0.20 |
| Interface | Jetpack Compose |
| Arquitetura | MVVM + Clean Architecture |
| Injeção de dependência | Koin 4.1.1 (com suporte a Workers via `KoinWorkerFactory`) |
| Persistência local | Room 2.8.4 (KSP2) |
| Processamento em background | WorkManager 2.9.0 |
| Rede | Retrofit2 + OkHttp3 |
| Carregamento de imagens | Coil |

---

## 🏗️ Arquitetura

O projeto segue **MVVM + Clean Architecture**, separando responsabilidades em camadas:

- **Presentation:** telas em Jetpack Compose e ViewModels.
- **Domain:** regras de negócio e contratos de repositório.
- **Data:** acesso à API (Retrofit), banco local (Room) e workers de sincronização.

---

## 🔄 Fluxo de sincronização offline-first

Para garantir que nenhum dado seja perdido no trabalho de campo:

```
[Usuário registra placa + foto]
            │
            ▼
 RegistrationRepository tenta enviar à API
            │
     ┌──────┴───────┐
  Sucesso         Falha de rede
     │                 │
     ▼                 ▼
   Fim        Salva PlacaSyncEntity + URI da foto (Room)
                       │
                       ▼
        SyncPlacaWorker enfileirado (NetworkType.CONNECTED)
                       │
                       ▼
        Conexão restaurada → Worker processa a fila,
        injeta o JWT, envia ao servidor e limpa o armazenamento local
```

Em texto:

1. O usuário preenche os dados e tira a foto da sinalização.
2. O `RegistrationRepository` tenta o envio direto para a API.
3. Em caso de falha (timeout ou sem conexão), o registro (`PlacaSyncEntity`) e a URI local da foto são salvos no SQLite via Room.
4. O `SyncPlacaWorker` é enfileirado com a restrição `NetworkType.CONNECTED`.
5. Quando o Android detecta o retorno da internet, o Worker processa a fila de forma assíncrona, injeta o token JWT, envia os pacotes ao servidor e limpa os dados locais.

---

## 🚀 Como executar localmente

### 1. Pré-requisitos

- [Android Studio](https://developer.android.com/studio) (versão mais recente recomendada)
- JDK 17 ou superior
- Dispositivo físico (depuração USB/Wireless) ou emulador configurado no AVD
- [API do Caça Placa (backend)](https://github.com/Aaron-GMM/ProjetoSocialBackend/) rodando localmente

### 2. Configuração do ambiente

O app precisa saber o endereço do backend. **Não use `localhost`**: no Android, ele aponta para o próprio dispositivo.

No arquivo `local.properties`, na raiz do projeto, adicione a URL base da API, usando o IP da sua máquina na rede local:

```properties
BASE_URL="http://192.168.100.26:8000/"
```

> 💡 **Dicas:**
> - No **emulador Android**, o endereço `http://10.0.2.2:8000/` aponta para a máquina host.
> - Em **dispositivo físico**, o celular e o computador devem estar na mesma rede Wi-Fi.
> - O arquivo `local.properties` não deve ser versionado (já é ignorado pelo `.gitignore` padrão do Android).

### 3. Build e execução

```bash
git clone <url-deste-repositorio>
```

1. Abra a pasta do projeto no Android Studio.
2. Aguarde o Gradle Sync baixar as dependências e gerar os arquivos do KSP.
3. Selecione o dispositivo de destino e clique em **Run** (`Shift + F10`).

---

## 🔗 Projetos relacionados

- **Backend (API):** [ProjetoSocialBackend](https://github.com/Aaron-GMM/ProjetoSocialBackend/)

---
