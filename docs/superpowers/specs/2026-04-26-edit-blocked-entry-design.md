# Design: Funzionalità di Modifica per Entry Bloccati

**Data:** 2026-04-26  
**Autore:** Claude Code  
**Stato:** Approvato

---

## Panoramica

Aggiungere la capacità di modificare i numeri già inseriti nella blacklist dell'app CallsBlocker. La finestra di modifica riutilizzerà il componente `AddEntryBottomSheet` con i dati precompilati, mantenendo coerenza UI e minimizzando duplicazione di codice.

---

## Requisiti Funzionali

1. **Bottone di Modifica**: Aggiungere un'icona "Modifica" (Edit) sulla scheda BlockedEntryItem accanto al bottone delete
2. **Apertura Precompilata**: Cliccando il bottone si apre un sheet con i valori dell'entry: numero, etichetta, modalità prefisso, azione selezionata
3. **Applicazione Immediata**: I cambiamenti salvati vengono applicati immediatamente al database e visibili in lista
4. **Rimozione Chip Azione**: Togliere il chip colorato (BLOCCA/SILENZIA/CONSENTI) dalla scheda, mantenendo solo numero, etichetta e flag prefisso

---

## Architettura

### Componenti Coinvolti

#### 1. **AddEntryBottomSheet** (modificato)
- **Nuovo parametro**: `editingEntry: BlockedEntry? = null`
- **Comportamento modale**:
  - Se `editingEntry == null` → Modalità "Aggiungi"
    - Titolo: "Aggiungi voce alla blacklist"
    - Bottone azione: "Aggiungi"
    - Campi: vuoti
  - Se `editingEntry != null` → Modalità "Modifica"
    - Titolo: "Modifica voce"
    - Bottone azione: "Salva"
    - Campi: precompilati con i valori di `editingEntry`
- **Callback**: Rinominare `onAddEntry: (BlockedEntry) -> Unit` a `onSaveEntry: (BlockedEntry) -> Unit`
  - Per l'inserimento: crea un `BlockedEntry` senza ID (Room assegnerà l'ID auto-generato)
  - Per la modifica: crea un `BlockedEntry` con lo stesso ID dell'entry originale
- **Validazione**: Mantiene la stessa logica di validazione del pattern

#### 2. **BlockedEntryItem** (modificato)
- **Nuovo callback**: `onEdit: (BlockedEntry) -> Unit = {}`
- **Layout modificato**: 
  - Rimuovere l'`AssistChip` colorato con l'azione (BLOCCA/SILENZIA/CONSENTI)
  - Aggiungere `IconButton` con `Icons.Filled.Edit` prima del bottone delete
  - Ordine da sinistra a destra: [Numero/Etichetta/Info] [Edit Icon] [Delete Icon]
- **Stile**: Icona Edit con colore `onSurfaceVariant` come il delete

#### 3. **MainViewModel** (modificato)
- **Nuovo metodo**:
  ```kotlin
  fun updateEntry(entry: BlockedEntry) {
      viewModelScope.launch {
          repository.update(entry)
      }
  }
  ```
- **Nota**: Non è necessario aggiornare `_uiState` manualmente perché il Flow da Room aggiornerà automaticamente `entries` quando il database cambia

#### 4. **HomeScreen** (modificato)
- **Nuovo state**: 
  ```kotlin
  var editingEntry by remember { mutableStateOf<BlockedEntry?>(null) }
  ```
- **Logica di callback**:
  - `BlockedEntryItem` riceve `onEdit = { entry -> editingEntry = entry }`
  - `AddEntryBottomSheet` riceve:
    - `editingEntry = editingEntry`
    - `onSaveEntry = { newEntry -> viewModel.updateEntry(newEntry) }`
    - `onDismiss = { editingEntry = null }`
- **Comportamento**: Quando l'utente modifica e salva, il sheet si chiude e la lista si aggiorna automaticamente via Flow

---

## Data Flow

```
HomeScreen (stato)
  ↓
  Utente clicca Edit su una voce
  ↓
  editingEntry = entry (stato locale HomeScreen)
  ↓
  AddEntryBottomSheet apre con editingEntry precompilato
  ↓
  Utente modifica e clicca "Salva"
  ↓
  onSaveEntry callback
  ↓
  viewModel.updateEntry(modifiedEntry)
  ↓
  repository.update(entry)
  ↓
  Room aggiorna il database
  ↓
  Flow<List<BlockedEntry>> emette nuova lista
  ↓
  MainViewModel aggiorna entries in UiState
  ↓
  HomeScreen ricompose con liste aggiornata
  ↓
  editingEntry = null (sheet si chiude)
  ↓
  UI mostra la voce modificata
```

---

## Dettagli Implementativi

### AddEntryBottomSheet: Logica di Precompilazione

```kotlin
// Inizializzazione campi
val (initialPattern, initialLabel, initialIsPrefix, initialAction) = 
    editingEntry?.let { entry ->
        Tuple4(entry.pattern, entry.label, entry.isPrefix, entry.action)
    } ?: Tuple4("", "", false, CallAction.BLOCK)

var pattern by remember { mutableStateOf(initialPattern) }
var label by remember { mutableStateOf(initialLabel) }
var isPrefix by remember { mutableStateOf(initialIsPrefix) }
var selectedAction by remember { mutableStateOf(initialAction) }
```

### BlockedEntryItem: Callback Edit

```kotlin
IconButton(
    onClick = { onEdit(entry) },
    modifier = Modifier.padding(0.dp)
) {
    Icon(
        imageVector = Icons.Filled.Edit,
        contentDescription = "Modifica",
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
```

### HomeScreen: Gestione Stato Edit

```kotlin
var editingEntry by remember { mutableStateOf<BlockedEntry?>(null) }

// ...

BlockedEntryItem(
    entry = entry,
    onEdit = { selectedEntry -> editingEntry = selectedEntry },
    onDelete = { entryId -> viewModel.deleteEntry(entryId) }
)

// ...

AddEntryBottomSheet(
    showSheet = editingEntry != null,
    editingEntry = editingEntry,
    onDismiss = { editingEntry = null },
    onSaveEntry = { modifiedEntry ->
        if (editingEntry != null) {
            // Modifica
            viewModel.updateEntry(modifiedEntry)
        } else {
            // Aggiunta
            viewModel.addEntry(modifiedEntry)
        }
        editingEntry = null
    }
)
```

---

## Testing

- **Unit tests**: Verificare che `MainViewModel.updateEntry()` chiama `repository.update()`
- **UI tests**: 
  - Tappare Edit su una voce apre il sheet
  - Campi sono precompilati
  - Modificare un campo e salvare aggiorna la lista
  - Il chip colorato non è più visibile nella scheda

---

## Backcompat e Migrazione

Nessuna migrazione necessaria — il database e la Room entity rimangono invariati. Il metodo `update()` già esiste nel DAO.

---

## Note di Implementazione

1. **ID Entry**: Assicurare che quando si crea il `BlockedEntry` modificato, l'ID rimane lo stesso dell'originale
2. **Timing**: L'aggiornamento della lista è automatico via Flow di Room — nessuna sincronizzazione manuale necessaria
3. **Stato**: Usare `remember` per `editingEntry` in HomeScreen per non perdere lo stato durante ricomposizioni
