import { useState } from 'react'

type StockEditorProps = {
    productId: number
    productName: string
    currentStock: number
    onSaved: (stock: number) => void
}

export default function StockEditor({
                                        productId,
                                        productName,
                                        currentStock,
                                        onSaved,
                                    }: StockEditorProps) {
    const [draft, setDraft] = useState(String(currentStock))
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')
    const [message, setMessage] = useState('')

    async function saveStock() {
        const stock = Number(draft)

        setError('')
        setMessage('')

        if (
            draft.trim() === '' ||
            !Number.isInteger(stock) ||
            stock < 0 ||
            stock > 2147483647
        ) {
            setError('Enter a whole number from 0 to 2,147,483,647.')
            return
        }

        setSaving(true)

        try {
            const response = await fetch(`/api/products/${productId}/stock`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ stock }),
            })

            if (!response.ok) {
                throw new Error(
                    response.status === 404
                        ? 'This product no longer exists.'
                        : `Could not save stock (${response.status}).`
                )
            }

            onSaved(stock)
            setDraft(String(stock))
            setMessage('Saved.')
        } catch (error) {
            setError(
                error instanceof Error ? error.message : 'Could not save stock.'
            )
        } finally {
            setSaving(false)
        }
    }

    return (
        <form
            onSubmit={(event) => {
                event.preventDefault()
                void saveStock()
            }}
        >
            <input
                aria-label={`Stock for ${productName}`}
                type="number"
                min="0"
                max="2147483647"
                step="1"
                required
                value={draft}
                disabled={saving}
                onChange={(event) => {
                    setDraft(event.target.value)
                    setError('')
                    setMessage('')
                }}
                style={{ width: '90px', marginRight: '8px' }}
            />

            <button type="submit" disabled={saving}>
                {saving ? 'Saving...' : 'Save'}
            </button>

            {error && <p role="alert">{error}</p>}
            {message && <p role="status">{message}</p>}
        </form>
    )
}